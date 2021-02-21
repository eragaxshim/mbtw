package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;

import java.util.Random;

public class BrickBlock extends Block implements Waterloggable {
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;
    public static final IntProperty BAKE_PROGRESS = IntProperty.of("bake_progress", 0, 8);

    public BrickBlock(Settings settings) {
        super(settings);
        Random random = new Random();
        setDefaultState(getStateManager().getDefaultState().with(WATERLOGGED, false).with(BAKE_PROGRESS, random.nextInt(8+1)));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    static {
        WATERLOGGED = Properties.WATERLOGGED;
        SHAPE = Block.createCuboidShape(5.0D, 0.0D, 2.0D, 11.0D, 4.0D, 14.0D);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return direction == Direction.DOWN && !this.canPlaceAt(state, world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return sideCoversSmallSquare(world, pos.down(), Direction.UP);
    }

    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
        this.tryBreakBrick(world, pos, entity);
        super.onSteppedOn(world, pos, entity);
    }

    public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
        this.tryBreakBrick(world, pos, entity);
        super.onLandedUpon(world, pos, entity, distance);
    }

    private void tryBreakBrick(World world, BlockPos pos, Entity entity)
    {
        if (this.breaksBrick(world, entity)) {
            if (!world.isClient) {
                BlockState state = world.getBlockState(pos);
                if (state.isOf(Mbtw.CLAY_BRICK)) {
                    int b = state.get(BAKE_PROGRESS);
                    world.breakBlock(pos, b != 8);
                }
            }
        }
    }

    private boolean breaksBrick(World world, Entity entity)
    {
        if (!(entity instanceof BatEntity)) {
            if (!(entity instanceof LivingEntity)) {
                return false;
            } else {
                return entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
            }
        } else {
            return false;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(WATERLOGGED);
        stateManager.add(BAKE_PROGRESS);
    }
}
