package mbtw.mbtw.block.entity;

import mbtw.mbtw.block.AbstractMechanicalBlock;
import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.recipe.AbstractMechanicalRecipe;
import mbtw.mbtw.state.property.MbtwProperties;
import net.minecraft.block.Block;
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
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBlockMechanicalProcessor extends LockableContainerBlockEntity implements MechanicalSinkBlockEntity {
    private boolean processing;
    private int availablePower;

    private BlockState connectorState;
    private BlockPos connectorPos;

    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    int cookTime;
    int cookTimeTotal;

    private final RecipeManager.MatchGetter<Inventory, ? extends AbstractMechanicalRecipe> matchGetter;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AbstractBlockMechanicalProcessor.this.cookTime;
                case 1 -> AbstractBlockMechanicalProcessor.this.cookTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AbstractBlockMechanicalProcessor.this.cookTime = value;
                case 1 -> AbstractBlockMechanicalProcessor.this.cookTimeTotal = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public AbstractBlockMechanicalProcessor(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, MechanicalSink sink, RecipeType<? extends AbstractMechanicalRecipe> recipeType) {
        super(blockEntityType, pos, state);
        this.processing = true;
        this.matchGetter = RecipeManager.createCachedMatchGetter(recipeType);
        this.connectorState = null;
        this.connectorPos = null;
    }

    public static void powerOff(World world, BlockPos pos, BlockState state, boolean updatedState) {
        if (state.get(MbtwProperties.POWERED) || updatedState) {
            world.setBlockState(pos, state.with(MbtwProperties.POWERED, false), Block.NOTIFY_ALL);
        }
    }

    public static void powerOn(World world, BlockPos pos, BlockState state, boolean updatedState) {
        if (!state.get(MbtwProperties.POWERED) || updatedState) {
            world.setBlockState(pos, state.with(MbtwProperties.POWERED, true), Block.NOTIFY_ALL);
        }
    }

    public static void recipeTick(World world, BlockPos pos, BlockState state, AbstractBlockMechanicalProcessor processor) {
        ItemStack inputStack = processor.inventory.get(0);
        boolean doMarkDirty = false;
        int cookTime = processor.cookTime;
        int cookTimeTotal = processor.cookTimeTotal;

        if (!inputStack.isEmpty()) {
            AbstractMechanicalRecipe recipe = processor.matchGetter.getFirstMatch(processor, world).orElse(null);
            if (recipe != null) {
                int maxOutPutSlotCount = Math.min(processor.getMaxCountOfSlot(1), processor.getMaxCountPerStack());
                boolean canAcceptRecipe = AbstractBlockMechanicalProcessor.canAcceptRecipeOutput(recipe, processor.inventory, maxOutPutSlotCount);
                if (canAcceptRecipe && processor.availablePower >= recipe.getRequiredPower()) {
                    processor.cookTime++;
                    processor.cookTimeTotal = recipe.getProcessingTime();
                    if (processor.cookTime > 0 && processor.cookTime == processor.cookTimeTotal) {
                        processor.cookTime = 0;
                        // Adds new item, decrements previous item
                        AbstractBlockMechanicalProcessor.craftRecipe(recipe, processor.inventory);
                    }
                    doMarkDirty = true;
                } else {
                    processor.cookTime = 0;
                }
            } else {
                processor.cookTime = 0;
            }
        }
        if (doMarkDirty || processor.cookTime != cookTime || processor.cookTimeTotal != cookTimeTotal) {
            AbstractBlockMechanicalProcessor.markDirty(world, pos, state);
        }
    }

    public abstract int getMaxCountOfSlot(int slotIndex);

    private static boolean canAcceptRecipeOutput(AbstractMechanicalRecipe recipe, DefaultedList<ItemStack> slots, int maxSlotCount) {
        // if nothing in input, no
        if (slots.get(0).isEmpty()) {
            return false;
        }
        ItemStack recipeOutput = recipe.getOutput();
        // if recipe is empty, false
        if (recipeOutput.isEmpty()) {
            return false;
        }
        ItemStack outputSlotStack = slots.get(1);
        // if output is empty, it is always good
        if (outputSlotStack.isEmpty()) {
            return true;
        }
        if (!outputSlotStack.isItemEqual(recipeOutput)) {
            return false;
        }
        int combinedCount = outputSlotStack.getCount() + recipeOutput.getCount();
        // Make sure combined count is less or equal to max counts
        return combinedCount <= maxSlotCount && combinedCount <= outputSlotStack.getMaxCount();
    }

    public static ItemStack getInputSlot(DefaultedList<ItemStack> slots) {
        return slots.get(0);
    }

    public static ItemStack getOutputSlot(DefaultedList<ItemStack> slots) {
        return slots.get(1);
    }

    public static void setOutputSlot(DefaultedList<ItemStack> slots, ItemStack stack) {
        slots.set(1, stack);
    }

    public static void craftRecipe(AbstractMechanicalRecipe recipe, DefaultedList<ItemStack> slots) {
        ItemStack inputStack = getInputSlot(slots);
        ItemStack recipeOutput = recipe.getOutput();
        ItemStack outputSlotStack = getOutputSlot(slots);
        if (outputSlotStack.isEmpty()) {
           setOutputSlot(slots, recipeOutput.copy());
        } else if (outputSlotStack.isOf(recipeOutput.getItem())) {
            outputSlotStack.setCount(outputSlotStack.getCount() + recipeOutput.getCount());
        }
        inputStack.decrement(1);
    }



    public static void serverTick(World world, BlockPos sinkPos, BlockState sinkState, AbstractBlockMechanicalProcessor processor) {
        MechanicalSinkBlockEntity.mechanicalTick(world, sinkPos, sinkState, processor);
        processor.availablePower = processor.sink().getAvailablePower(world, sinkState, sinkPos, processor);
        recipeTick(world, sinkPos, sinkState, processor);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("CookTime", (short)this.cookTime);
        nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        Inventories.writeNbt(nbt, this.inventory);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
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
        ItemStack itemStack = this.inventory.get(slot);
        // inputting nonempty, putting it in same stack with also same nbt
        boolean alreadyThere = !stack.isEmpty() && stack.isItemEqual(itemStack) && ItemStack.areNbtEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        int maxOutPutSlotCount = Math.min(this.getMaxCountOfSlot(slot), this.getMaxCountPerStack());
        if (stack.getCount() > maxOutPutSlotCount) {
            stack.setCount(maxOutPutSlotCount);
        }
        if (slot == 0 && !alreadyThere) {
            this.cookTimeTotal = AbstractBlockMechanicalProcessor.getProcessingTime(this.world, this);
            this.cookTime = 0;
            this.markDirty();
        }
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

    private static int getProcessingTime(World world, AbstractBlockMechanicalProcessor processor) {
        return processor.matchGetter.getFirstMatch(processor, world).map(AbstractMechanicalRecipe::getProcessingTime).orElse(200);
    }

    @Override
    public void worldSetAvailablePower(World world, BlockPos sinkPos, BlockState sinkState, int availablePower) {
        this.availablePower = availablePower;
        BlockState newState;
        if (availablePower > 0) {
            newState = sinkState.with(AbstractMechanicalBlock.POWERED, true).with(AbstractMechanicalBlock.MECHANICAL_SINK, availablePower);
        } else {
            newState = sinkState.with(AbstractMechanicalBlock.POWERED, false);
        }

        world.setBlockState(sinkPos, newState);
    }

    @Override
    public void worldSetSink(World world, BlockPos sinkPos, BlockState sinkState, int sink) {
        world.setBlockState(sinkPos, sinkState.with(AbstractMechanicalBlock.MECHANICAL_SINK, sink));
    }

    @Override
    public int getAvailablePower(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return sink().getAvailablePower(world, state, pos, blockEntity);
    }

    @Override
    public int getSink(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return sink().getSink(world, state, pos, blockEntity);
    }
}
