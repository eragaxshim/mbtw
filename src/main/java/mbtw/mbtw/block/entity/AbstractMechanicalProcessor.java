package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.MechanicalSink;
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
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractMechanicalProcessor extends LockableContainerBlockEntity {
    private boolean processing;
    private int availablePower;

    private BlockState connectorState;
    private BlockPos connectorPos;

    private static final MechanicalSink SINK = (MechanicalSink) Mbtw.MILLSTONE;

    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    int cookTime;
    int cookTimeTotal;

    private final RecipeManager.MatchGetter<Inventory, ? extends AbstractCookingRecipe> matchGetter;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AbstractMechanicalProcessor.this.cookTime;
                case 1 -> AbstractMechanicalProcessor.this.cookTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AbstractMechanicalProcessor.this.cookTime = value;
                case 1 -> AbstractMechanicalProcessor.this.cookTimeTotal = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public AbstractMechanicalProcessor(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
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

    public static void updatePowered(World world, BlockPos sinkPos, BlockState sinkState, AbstractMechanicalProcessor processor, boolean updatedState) {
        if (processor.availablePower <= 0 || !processor.processing) {
            powerOff(world, sinkPos, sinkState, updatedState);
        } else {
            powerOn(world, sinkPos, sinkState, updatedState);
        }
    }

    public static void recipeTick(World world, BlockPos pos, BlockState state, AbstractMechanicalProcessor processor) {
        ItemStack inputStack = processor.inventory.get(0);
        boolean doMarkDirty = false;
        int cookTime = processor.cookTime;
        int cookTimeTotal = processor.cookTimeTotal;

        if (!inputStack.isEmpty()) {
            AbstractCookingRecipe recipe = processor.matchGetter.getFirstMatch(processor, world).orElse(null);
            if (recipe != null) {
                int maxOutPutSlotCount = Math.min(processor.getMaxCountOfSlot(1), processor.getMaxCountPerStack());
                boolean canAcceptRecipe = AbstractMechanicalProcessor.canAcceptRecipeOutput(recipe, processor.inventory, maxOutPutSlotCount);
                if (canAcceptRecipe) {
                    processor.cookTime++;
                    processor.cookTimeTotal = recipe.getCookTime();
                    if (processor.cookTime > 0 && processor.cookTime == processor.cookTimeTotal) {
                        processor.cookTime = 0;
                        // Adds new item, decrements previous item
                        AbstractMechanicalProcessor.craftRecipe(recipe, processor.inventory);
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
            AbstractMechanicalProcessor.markDirty(world, pos, state);
        }
    }

    public abstract int getMaxCountOfSlot(int slotIndex);

    private static boolean canAcceptRecipeOutput(AbstractCookingRecipe recipe, DefaultedList<ItemStack> slots, int maxSlotCount) {
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

    public static void craftRecipe(AbstractCookingRecipe recipe, DefaultedList<ItemStack> slots) {
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

    public boolean isProcessing() {
        return this.propertyDelegate.get(0) > 0;
    }

    public static void serverTick(World world, BlockPos sinkPos, BlockState sinkState, AbstractMechanicalProcessor processor) {
        recipeTick(world, sinkPos, sinkState, processor);
//        BlockState cState = processor.connectorState;
//        BlockPos cPos = processor.connectorPos;
//        // connectorPos and connectorState are set by block updates
//        if (cPos != null && cState != null && cState.getBlock() instanceof MechanicalConnector connector) {
//            int connectorSource = connector.getSource(cState);
//            int connectorSink = connector.getSink(cState);
//            if (connector.getBearing(cState)) {
//                processor.availablePower = connectorSink;
//            } else {
//                processor.availablePower = 0;
//            }
//
//            int sink = SINK.getSink(sinkState);
//            // If not sinking equal to source (and not already at max sink), set it to appropriate value
//            if (sink != connectorSource && sink < SINK.getMaxSink()) {
//                BlockState newState = sinkState.with(MillstoneBlock.MECHANICAL_SINK, Math.min(SINK.getMaxSink(), connectorSource));
//                updatePowered(world, sinkPos, newState, processor, true);
//                return;
//            }
//        } else {
//            processor.availablePower = 0;
//        }
//
//        updatePowered(world, sinkPos, sinkState, processor, false);
//
//        if (world.getTime() % 43 == 0) {
//            System.out.println("null millstone");
//        }
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
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        // !bl means
        if (slot == 0 && !alreadyThere) {
            this.cookTimeTotal = AbstractMechanicalProcessor.getCookTime(this.world, this);
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

    }

    private static int getCookTime(World world, AbstractMechanicalProcessor processor) {
        return processor.matchGetter.getFirstMatch(processor, world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }
}
