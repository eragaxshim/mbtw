package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.entity.MechanicalHopperBlockEntity;
import mbtw.mbtw.block.entity.MechanicalSinkBlockEntity;
import mbtw.mbtw.block.entity.MillstoneBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MechanicalHopper extends HopperBlock implements MechanicalSink {
    private final List<Direction> INPUT_FACES = List.of(Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH);

    public MechanicalHopper(Settings settings) {
        super(settings);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != Mbtw.MECHANICAL_HOPPER_ENTITY || world.isClient) {
            return null;
        }

        return (world1, pos, tickState, hopper) -> MechanicalHopperBlockEntity.serverTick(world1, pos, tickState, (MechanicalHopperBlockEntity) hopper);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MechanicalHopperBlockEntity(pos, state);
    }

    @Override
    public int getMaxSink(BlockState state) {
        return 4;
    }

    @Override
    public int getSink(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return MechanicalSinkBlockEntity.blockGetSink(world, pos, blockEntity);
    }

    @Override
    public boolean isPowered(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return false;
    }

    public static void changeFilter() {

    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MechanicalHopperBlockEntity) {

                player.openHandledScreen((MechanicalHopperBlockEntity)blockEntity);
                player.incrementStat(Stats.INSPECT_HOPPER);
            }

            return ActionResult.CONSUME;
        }
    }

    @Override
    public boolean isSinkAtFace(BlockState state, Direction sinkFace) {
        // Horizontal
        return sinkFace.getVector().getY() == 0;
    }

    @Override
    public List<Direction> getInputFaces(BlockState state) {
        return INPUT_FACES;
    }

    @Override
    public boolean incongruentInputAllowed(BlockState state) {
        return false;
    }
}
