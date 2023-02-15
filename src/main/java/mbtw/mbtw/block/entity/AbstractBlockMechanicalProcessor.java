package mbtw.mbtw.block.entity;

import mbtw.mbtw.block.AbstractMechanicalBlock;
import mbtw.mbtw.recipe.PoweredRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractBlockMechanicalProcessor extends AbstractBlockProcessorEntity implements MechanicalSinkBlockEntity {
    protected int sink;

    public AbstractBlockMechanicalProcessor(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, RecipeType<? extends PoweredRecipe> recipeType) {
        super(blockEntityType, pos, state, 2, recipeType);
    }

    public static void serverTick(World world, BlockPos sinkPos, BlockState sinkState, AbstractBlockMechanicalProcessor processor) {
        MechanicalSinkBlockEntity.mechanicalTick(world, sinkPos, sinkState, processor);
        processor.availablePower = processor.sinkBlock().getAvailablePower(world, sinkState, sinkPos, processor);
        recipeTick(world, sinkPos, sinkState, processor);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.sink = nbt.getShort("Sink");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("Sink", (short)this.sink);
    }

    @Override
    public void worldSetAvailablePower(World world, BlockState sinkState, BlockPos sinkPos, int availablePower) {
        if (this.availablePower == availablePower) {
            return;
        }

        this.availablePower = availablePower;
        BlockState newState;
        if (availablePower > 0) {
            newState = sinkState.with(AbstractMechanicalBlock.POWERED, true);
            this.sink = availablePower;
        } else {
            newState = sinkState.with(AbstractMechanicalBlock.POWERED, false);
        }
        markDirty();
        world.setBlockState(sinkPos, newState, Block.NOTIFY_ALL);
    }

    @Override
    public int getAvailablePower() {
        return this.availablePower;
    }

    @Override
    public int getSink() {
        return this.sink;
    }

    @Override
    public void setAvailablePower(int availablePower) {
        this.availablePower = availablePower;
    }

    @Override
    public void setSink(int sink) {
        this.sink = sink;
    }
}
