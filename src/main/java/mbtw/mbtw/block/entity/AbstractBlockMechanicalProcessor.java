package mbtw.mbtw.block.entity;

import mbtw.mbtw.block.AbstractMechanicalBlock;
import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.recipe.AbstractMechanicalRecipe;
import mbtw.mbtw.recipe.PoweredRecipe;
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

public abstract class AbstractBlockMechanicalProcessor extends AbstractBlockProcessorEntity implements MechanicalSinkBlockEntity {
    public AbstractBlockMechanicalProcessor(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, RecipeType<? extends PoweredRecipe> recipeType) {
        super(blockEntityType, pos, state, 2, recipeType);
    }

    public static void serverTick(World world, BlockPos sinkPos, BlockState sinkState, AbstractBlockMechanicalProcessor processor) {
        MechanicalSinkBlockEntity.mechanicalTick(world, sinkPos, sinkState, processor);
        processor.availablePower = processor.sink().getAvailablePower(world, sinkState, sinkPos, processor);
        recipeTick(world, sinkPos, sinkState, processor);
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
