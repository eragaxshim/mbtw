package mbtw.mbtw.inventory;

import mbtw.mbtw.block.MbtwApi;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class InventoryMover {
    private BlockApiCache<Storage<ItemVariant>, Direction> storageLookup;

    // Side facing target
    private Direction moverFace;

    public InventoryMover(ServerWorld world, BlockPos targetPos, Direction moverFace) {
        create(world, targetPos, moverFace);
    }

    private void create(ServerWorld world, BlockPos pos, Direction moverFace) {
        this.storageLookup = BlockApiCache.create(ItemStorage.SIDED, world, pos);
        this.moverFace = moverFace;
    }

    public void updateDirection(ServerWorld world, BlockPos pos, Direction moverFace) {
        create(world, pos, moverFace);
    }

    // Adapted from HopperBlockEntity
    private Storage<ItemVariant> getEntityStorage(World world, BlockPos pos, Direction direction) {
        List<Entity> list = world.getOtherEntities(null, new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY(), pos.getZ() + 1), EntityPredicates.VALID_INVENTORIES);
        if (!list.isEmpty()) {
            Inventory targetInventory = (Inventory)list.get(world.random.nextInt(list.size()));
            return InventoryStorage.of(targetInventory, direction);
        } else {
            return null;
        }
    }

    public boolean insert(World world, BlockPos moverPos, Inventory inventory) {
        // We want inventory available on other block's side facing this one
        Storage<ItemVariant> target = storageLookup.find(moverFace.getOpposite());

        Storage<ItemVariant> entityStorage;
        if (target == null && (entityStorage = getEntityStorage(world, moverPos.offset(moverFace), moverFace)) != null) {
            target = entityStorage;
        }

        long moved = StorageUtil.move(
                InventoryStorage.of(inventory, moverFace),
                target,
                iv -> true,
                1,
                null
        );
        return moved == 1;
    }

    /**
     * Attempts to insert a single ItemStack into the storage. The original stack is not modified.
     * @return A copy of the original ItemStack with the leftover count
     */
    public static ItemStack insertSingleStack(Storage<ItemVariant> storage, ItemStack stack) {
        ItemStack newStack = stack.copy();
        int stackCount = stack.getCount();
        ItemVariant resource = ItemVariant.of(newStack);

        try (Transaction transferTransaction = Transaction.openOuter()) {
            // check how much can be inserted
            long inserted = storage.insert(resource, stackCount, transferTransaction);

            if (inserted > 0 && inserted <= stackCount) {
                transferTransaction.commit();
                newStack.decrement((int) inserted);
            }
        }

        return newStack;
    }

    /**
     * If an ItemVariant satisfies the filter, it will remove this item from {@code from} and return it as an ItemStack,
     * but NOT insert it into {@code to}. If it does not satisfy the filter, it will be moved to {@code to} and an empty
     * ItemStack will be returned. If nothing can be moved, null will be returned.
     * <p>
     * Caller is responsible to abort the transaction if the filter item cannot be crafted.
     */
    public static ItemStack moveGetFiltered(Storage<ItemVariant> from, Storage<ItemVariant> to, Predicate<ItemVariant> filter, @Nullable TransactionContext transaction) {
        Objects.requireNonNull(filter, "Filter may not be null");
        Objects.requireNonNull(from, "From may not be null");
        Objects.requireNonNull(to, "To may not be null");

        ItemStack returnedStack = null;
        try (Transaction iterationTransaction = Transaction.openNested(transaction)) {
            for (StorageView<ItemVariant> view : from) {
                if (view.isResourceBlank()) continue;
                ItemVariant resource = view.getResource();

                // If it satisfies filter, extract it
                // Caller is responsible to abort if it cannot be crafted
                if (filter.test(resource)) {
                    try (Transaction filterTransaction = iterationTransaction.openNested()) {
                        long extract = view.extract(resource, 1, filterTransaction);
                        if (extract == 1) {
                            filterTransaction.commit();
                            // Return the filtered stack, so something can be done with it
                            returnedStack = resource.toStack(1);
                            // break from for loop
                            break;
                        }
                    }
                    // If it didn't break, continue
                    continue;
                }

                try (Transaction transferTransaction = iterationTransaction.openNested()) {
                    long accepted = to.insert(resource, 1, transferTransaction);
                    // extract it, or rollback if the amounts don't match
                    if (accepted == 1 && view.extract(resource, 1, transferTransaction) == 1) {
                        transferTransaction.commit();
                        returnedStack = ItemStack.EMPTY;
                        break;
                    }
                }
            }

            iterationTransaction.commit();
        }

        return returnedStack;
    }

    public boolean entityExtractAndFilter(World world, BlockPos filterPos, BlockState filterState, Filter filter, Inventory filterInventory, ItemEntity itemEntity) {
        ItemStack oldStack = itemEntity.getStack().copy();
        ItemStack newStack;
        if (filter.getFilterPredicate().test(ItemVariant.of(oldStack))) {
            newStack = filter.doFilter(world, filterPos, filterState, oldStack);
        } else {
            newStack = insertSingleStack(InventoryStorage.of(filterInventory, null), oldStack);
        }

        if (newStack == null || oldStack.isItemEqual(newStack)) {
            // Nothing happened
            return false;
        } else if (newStack.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setStack(newStack);
        }

        return true;
    }

    public boolean extractAndFilter(World world, BlockPos filterPos, BlockState filterState, Filter filter, Inventory filterInventory) {
        // We want inventory available on target's side facing this one
        Storage<ItemVariant> target = storageLookup.find(moverFace.getOpposite());

        if (target == null) {
            return false;
        }

        boolean extracted = false;
        try (Transaction extractTransaction = Transaction.openOuter()) {
            ItemStack filteredStack = moveGetFiltered(
                    target,
                    InventoryStorage.of(filterInventory, moverFace),
                    filter.getFilterPredicate(),
                    extractTransaction
            );
            boolean performFilter = filteredStack != null && !filteredStack.isEmpty();

            if (performFilter) {
                ItemStack afterFilter = filter.doFilter(world, filterPos, filterState, filteredStack);
                // If non-empty, not everything was used, which is required in this case
                if (afterFilter != null && afterFilter.isEmpty()) {
                    extracted = true;
                    extractTransaction.commit();
                }
            } else {
                extracted = filteredStack != null;
                extractTransaction.commit();
            }
        }
        return extracted;
    }

    public Direction getMoverFace() {
        return moverFace;
    }

    public interface Filter {
        Predicate<ItemVariant> getFilterPredicate();

        /**
         * This can perform side effects on the world and on itself.
         * @return Leftover ItemStack after performing filter
         */
        ItemStack doFilter(World world, BlockPos filterPos, BlockState state, ItemStack addedStack);
    }
}
