package mbtw.mbtw.block.entity;

import mbtw.mbtw.recipe.PoweredRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractBlockProcessorEntity extends LockableContainerBlockEntity {
    protected final int inventorySize;

    // If set by NBT they don't need default values
    protected int availablePower;
    int processingTime;
    int processingTimeTotal;

    protected DefaultedList<ItemStack> inventory;

    private final RecipeManager.MatchGetter<Inventory, ? extends PoweredRecipe> matchGetter;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AbstractBlockProcessorEntity.this.processingTime;
                case 1 -> AbstractBlockProcessorEntity.this.processingTimeTotal;
                case 2 -> AbstractBlockProcessorEntity.this.availablePower;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AbstractBlockProcessorEntity.this.processingTime = value;
                case 1 -> AbstractBlockProcessorEntity.this.processingTimeTotal = value;
                case 2 -> AbstractBlockProcessorEntity.this.availablePower = value;
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    public AbstractBlockProcessorEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, int inventorySize, RecipeType<? extends PoweredRecipe> recipeType) {
        super(blockEntityType, pos, state);
        this.inventorySize = inventorySize;
        this.inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        this.matchGetter = RecipeManager.createCachedMatchGetter(recipeType);
    }

    public static void recipeTick(World world, BlockPos pos, BlockState state, AbstractBlockProcessorEntity processor) {
        DefaultedList<ItemStack> inputStacks = getInputSlots(processor.inventory);
        boolean doMarkDirty = false;
        int cookTime = processor.processingTime;
        int cookTimeTotal = processor.processingTimeTotal;

        if (inputStacks.stream().anyMatch(stack -> !stack.isEmpty())) {
            PoweredRecipe recipe = processor.matchGetter.getFirstMatch(processor, world).orElse(null);
            if (recipe != null) {
                int maxOutputSlotCount = Math.min(processor.getMaxCountOfSlot(0), processor.getMaxCountPerStack());
                DynamicRegistryManager registryManager = world.getRegistryManager();
                boolean canAcceptRecipe = AbstractBlockProcessorEntity.canAcceptRecipeOutput(registryManager, recipe, processor.inventory, maxOutputSlotCount);
                if (canAcceptRecipe && processor.getAvailablePower() >= recipe.getRequiredPower()) {
                    processor.processingTime++;
                    processor.processingTimeTotal = recipe.getProcessingTime();
                    if (processor.processingTime > 0 && processor.processingTime == processor.processingTimeTotal) {
                        processor.processingTime = 0;
                        // Adds new item, decrements previous item
                        AbstractBlockProcessorEntity.craftRecipe(registryManager, recipe, processor.inventory);
                    }
                    doMarkDirty = true;
                } else {
                    processor.processingTime = 0;
                }
            } else {
                processor.processingTime = 0;
            }
        }
        if (doMarkDirty || processor.processingTime != cookTime || processor.processingTimeTotal != cookTimeTotal) {
            AbstractBlockProcessorEntity.markDirty(world, pos, state);
        }
    }

    public abstract int getMaxCountOfSlot(int slotIndex);

    private static boolean canAcceptRecipeOutput(DynamicRegistryManager registryManager, PoweredRecipe recipe, DefaultedList<ItemStack> slots, int maxSlotCount) {
        // if nothing in input, no
        if (getInputSlots(slots).stream().allMatch(ItemStack::isEmpty)) {
            return false;
        }
        ItemStack recipeOutput = recipe.getOutput(registryManager);
        // if recipe is empty, false
        if (recipeOutput.isEmpty()) {
            return false;
        }
        ItemStack outputSlotStack = slots.get(0);
        // if output is empty, it is always good
        if (outputSlotStack.isEmpty()) {
            return true;
        }
        // TODO allow for NBT
        if (!outputSlotStack.isItemEqual(recipeOutput)) {
            return false;
        }
        int combinedCount = outputSlotStack.getCount() + recipeOutput.getCount();
        // Make sure combined count is less or equal to max counts
        return combinedCount <= maxSlotCount && combinedCount <= outputSlotStack.getMaxCount();
    }

    public static DefaultedList<ItemStack> getInputSlots(DefaultedList<ItemStack> slots) {
        if (slots.size() > 1) {
            DefaultedList<ItemStack> newList = DefaultedList.ofSize(slots.size()-1);
            newList.addAll(slots.subList(1, slots.size()));
            return newList;
        } else {
            return DefaultedList.of();
        }
    }

    public static ItemStack getOutputSlot(DefaultedList<ItemStack> slots) {
        return slots.get(0);
    }

    public static void setOutputSlot(DefaultedList<ItemStack> slots, ItemStack stack) {
        slots.set(0, stack);
    }

    public static void craftRecipe(DynamicRegistryManager registryManager, PoweredRecipe recipe, DefaultedList<ItemStack> slots) {
        DefaultedList<ItemStack> inputStacks = getInputSlots(slots);
        ItemStack recipeOutput = recipe.getOutput(registryManager);
        ItemStack outputSlotStack = getOutputSlot(slots);
        // We have already checked if the output is at max count
        if (outputSlotStack.isEmpty()) {
            setOutputSlot(slots, recipeOutput.copy());
        } else if (outputSlotStack.isOf(recipeOutput.getItem())) {
            outputSlotStack.setCount(outputSlotStack.getCount() + recipeOutput.getCount());
        }
        recipe.decrementInput(inputStacks);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.processingTimeTotal = nbt.getShort("ProcessingTimeTotal");
        this.processingTime = nbt.getShort("ProcessingTime");
        this.availablePower = nbt.getShort("AvailablePower");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("AvailablePower", (short)this.availablePower);
        nbt.putShort("ProcessingTime", (short)this.processingTime);
        nbt.putShort("ProcessingTimeTotal", (short)this.processingTimeTotal);
        Inventories.writeNbt(nbt, this.inventory);
    }

    @Override
    public int size() {
        return inventorySize;
    }

    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.inventory, slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }

        return itemStack;
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean alreadyThere = !stack.isEmpty() && stack.isItemEqual(itemStack) && ItemStack.areNbtEqual(stack, itemStack);

        this.inventory.set(slot, stack);
        int maxOutputSlotCount = Math.min(this.getMaxCountOfSlot(slot), this.getMaxCountPerStack());
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(maxOutputSlotCount);
        }

        if (slot > 0 && !alreadyThere) {
            this.processingTimeTotal = AbstractBlockProcessorEntity.getProcessingTime(this.world, this);
            this.processingTime = 0;

        }

        this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world == null) {
            return false;
        }
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    private static int getProcessingTime(World world, AbstractBlockProcessorEntity processor) {
        return processor.matchGetter.getFirstMatch(processor, world).map(PoweredRecipe::getProcessingTime).orElse(200);
    }

    protected int getAvailablePower() {
        return availablePower;
    }
}

