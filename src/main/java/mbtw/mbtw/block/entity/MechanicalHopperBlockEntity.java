package mbtw.mbtw.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.HopperConversionStore;
import mbtw.mbtw.block.HopperFilter;
import mbtw.mbtw.block.MbtwApi;
import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.inventory.BlockStateInventory;
import mbtw.mbtw.inventory.FilterInventory;
import mbtw.mbtw.inventory.InventoryMover;
import mbtw.mbtw.recipe.HopperRecipe;
import mbtw.mbtw.util.NbtUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.Predicate;

public class MechanicalHopperBlockEntity extends LockableContainerBlockEntity
        implements MechanicalSinkBlockEntity, HopperConversionStore, FilterInventory, Hopper, InventoryMover.Filter {
    private int sink;
    private int availablePower;
    private Block filter;
    private final DefaultedList<ItemStack> inventory;
    private int transferCooldown;
    private ItemStack inFilter = ItemStack.EMPTY;
    private HopperFilter filterPredicate = null;
    private InventoryMover extractSource = null;
    private InventoryMover insertTarget = null;
    private BlockState connectedState = null;

    private final Map<Identifier, Integer> conversionProgress = new Object2IntOpenHashMap<>();

    private final RecipeManager.MatchGetter<FilterInventory, ? extends HopperRecipe> matchGetter;

    public MechanicalHopperBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.MECHANICAL_HOPPER_ENTITY, pos, state);
        this.matchGetter = RecipeManager.createCachedMatchGetter(Mbtw.HOPPER_FILTERING);
        this.inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
        this.transferCooldown = -1;
    }

    @Override
    public MechanicalSink sinkBlock() {
        return (MechanicalSink) Mbtw.MECHANICAL_HOPPER;
    }

    private void spawnItem(World world, ItemStack stack, BlockPos pos) {
        double x = pos.getX() + world.random.nextTriangular(0.5, 0.03);
        double y = pos.getY() + world.random.nextTriangular(0.8, 0.03);
        double z = pos.getZ() + world.random.nextTriangular(0.5, 0.03);

        ItemEntity itemEntity = new ItemEntity(world, x, y, z, stack);
        itemEntity.setVelocity(
                world.random.nextTriangular(0, 0.0172275),
                world.random.nextTriangular(0.35, 0.0172275*3),
                world.random.nextTriangular(0, 0.0172275)
        );
        world.spawnEntity(itemEntity);
    }

    public boolean insert(World world, BlockPos pos, BlockState state) {
        Direction hopperFacing;
        if (insertTarget == null) {
            hopperFacing = state.get(Properties.HOPPER_FACING);
            insertTarget = new InventoryMover((ServerWorld) world, pos.offset(hopperFacing), hopperFacing);
        } else if (insertTarget.getMoverFace() != (hopperFacing = state.get(Properties.HOPPER_FACING))) {
            insertTarget.updateDirection((ServerWorld) world, pos.offset(hopperFacing), hopperFacing);
        }
        return insertTarget.insert(world, pos, this);
    }

    public boolean extract(World world, BlockPos pos, BlockState state) {
        if (extractSource == null) {
            extractSource = new InventoryMover((ServerWorld) world, pos.offset(Direction.UP), Direction.UP);
        }
        return extractSource.extractAndFilter(world, pos, state, this, this);

    }

    public boolean extract(World world, BlockPos pos, BlockState state, ItemEntity itemEntity) {
        if (extractSource == null) {
            extractSource = new InventoryMover((ServerWorld) world, pos.offset(Direction.UP), Direction.UP);
        }
        return extractSource.entityExtractAndFilter(world, pos, state, this, this, itemEntity);
    }

    public void insertAndExtract(World world, BlockPos pos, BlockState state) {
        boolean insertedOrExtracted = insert(world, pos, state);

        insertedOrExtracted |= extract(world, pos, state);

        if (insertedOrExtracted) {
            transferCooldown = 8;
            markDirty(world, pos, state);
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, MechanicalHopperBlockEntity blockEntity) {
        MechanicalSinkBlockEntity.mechanicalTick(world, pos, state, blockEntity);

        --blockEntity.transferCooldown;
        if (blockEntity.transferCooldown <= 0) {
            blockEntity.transferCooldown = 0;
            blockEntity.insertAndExtract(world, pos, state);
        }
    }

    // Ensure this is onl run on the server
    public void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof ItemEntity itemEntity && VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())), this.getInputAreaShape(), BooleanBiFunction.AND)) {
            if (transferCooldown <= 0 && this.extract(world, pos, state, itemEntity)) {
                transferCooldown = 8;
                markDirty(world, pos, state);
            }
        }
    }

    @Override
    public int getAvailablePower() {
        return availablePower;
    }

    @Override
    public int getSink() {
        return sink;
    }

    @Override
    public void setAvailablePower(int availablePower) {
        this.availablePower = availablePower;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.filter = NbtUtil.blockFromNbt(nbt, "Filter");
        this.filterPredicate = MbtwApi.HOPPER_FILTER_API.find(this.filter);
        this.availablePower = nbt.getShort("AvailablePower");
        this.sink = nbt.getShort("Sink");
        NbtList nbtList = nbt.getList("ConversionProgress", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound conversionNbt = nbtList.getCompound(i);
            conversionProgress.put(new Identifier(conversionNbt.getString("identifier")), conversionNbt.getInt("value"));
        }
        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtUtil.writeBlockToNbt(nbt, "Filter", this.filter);
        nbt.putShort("Sink", (short)this.sink);
        nbt.putShort("AvailablePower", (short)this.availablePower);
        //nbt.putShort("ConversionProgress", (short)this.conversionProgress);
        NbtList nbtList = new NbtList();
        conversionProgress.forEach((key, value) -> {
            NbtCompound conversionNbt = new NbtCompound();
            conversionNbt.putString("identifier", key.toString());
            conversionNbt.putInt("value", value);
            nbtList.add(conversionNbt);
        });
        nbt.put("ConversionProgress", nbtList);
        nbt.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("mbtw.container.mechanical_hopper");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new HopperScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void setSink(int sink) {
        this.sink = sink;
    }

    @Override
    public int incrementConversionProgress(Identifier conversionType) {
        int current = conversionProgress.getOrDefault(conversionType, 0);
        int newValue = current+1;
        conversionProgress.put(conversionType, newValue);
        this.markDirty();
        return newValue;
    }

    @Override
    public void resetConversionProgress(Identifier conversionType) {
        conversionProgress.put(conversionType, 0);
        this.markDirty();
    }

    public BlockState getFilterModel() {
        if (filter == null) {
            return null;
        }
        return filter.getDefaultState();
    }

    // This is necessary to render the filter on game load
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    // We need to send the filter NBT to the client
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtUtil.writeBlockToNbt(nbt, "Filter", filter);
        return nbt;
    }

    // Used to ensure proper update when changing filter
    private void updateListeners() {
        this.markDirty();
        World world = this.getWorld();
        if (world != null) {
            world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    public int size() {
        return 5;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public BlockState getConnectedState(int index) {
        if (index == 0) {
            return connectedState;
        } else {
            throw new IndexOutOfBoundsException("Only index 0 is a valid index!");
        }
    }

    @Override
    public void setConnectedState(int index, BlockState state) {
        if (index == 0) {
            connectedState = state;
        } else {
            throw new IndexOutOfBoundsException("Only index 0 is a valid index!");
        }
    }

    @Override
    public int connectedSize() {
        return 1;
    }

    @Override
    public Direction connectedDirection(int index) {
        if (index == 0) {
            return Direction.DOWN;
        } else {
            throw new IndexOutOfBoundsException("Only index 0 is a valid index!");
        }
    }

    @Override
    public void putInFilter(ItemStack stack) {
        this.inFilter = stack;
    }

    @Override
    public ItemStack inFilter() {
        return inFilter;
    }

    @Override
    public void clearInFilter() {
        inFilter = ItemStack.EMPTY;
    }

    @Override
    public Block getFilter() {
        return this.filter;
    }

    @Override
    public void setFilter(Block filter) {
        this.filter = filter;
        this.filterPredicate = MbtwApi.HOPPER_FILTER_API.find(filter);

        this.updateListeners();
    }

    public double getHopperX() {
        return (double)this.pos.getX() + 0.5;
    }

    public double getHopperY() {
        return (double)this.pos.getY() + 0.5;
    }

    public double getHopperZ() {
        return (double)this.pos.getZ() + 0.5;
    }

    @Override
    public Predicate<ItemVariant> getFilterPredicate() {
        if (filterPredicate == null) {
            return (iv) -> true;
        } else {
            return filterPredicate::test;
        }
    }

    @Override
    public ItemStack doFilter(World world, BlockPos filterPos, BlockState state, ItemStack addedStack) {
        // Update states for use in recipe
        BlockStateInventory.updateStates(world, filterPos, this);
        putInFilter(addedStack);
        HopperRecipe recipe = matchGetter.getFirstMatch(this, world).orElse(null);
        clearInFilter();
        if (recipe == null) {
            return null;
        }
        // Get output
        int inputStackCount = addedStack.getCount();

        int timesCraftable = recipe.timesCraftable(inputStackCount);
        ItemStack output = recipe.craft(this, world.getRegistryManager());

        // For each time we craft we want to get the recipe output
        output.setCount(output.getCount()*timesCraftable);

        // We decrement the input stack
        ItemStack returnedStack = addedStack.copy();

        recipe.decrementInput(returnedStack, timesCraftable);



        //TODO check hopper direction

        if (recipe.conversionRecipe(world).convert(world, filterPos, this, this, timesCraftable)) {
            // We spawn the output
            spawnItem(world, output, filterPos);
        };

        return returnedStack;
    }
}
