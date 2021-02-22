package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.entity.BrickBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;

import java.util.Random;

public class BrickBlock extends Block implements Waterloggable, BlockEntityProvider {
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;
    public static final IntProperty BAKE_PROGRESS = IntProperty.of("bake_progress", 0, 8);

    public BrickBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(WATERLOGGED, false).with(BAKE_PROGRESS, 0));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new BrickBlockEntity();
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        long time = world.getTimeOfDay();
        if (state.get(BAKE_PROGRESS) != 8 && (time < 12500 || time > 23500) && world.isSkyVisible(pos) && !world.isRaining()) {
            for(int i = 0; i < random.nextInt(2) + 2; ++i) {
                world.addParticle(ParticleTypes.SMOKE, (double)pos.getX() + 0.5D + (random.nextFloat() - 0.5), (double)pos.getY() + 0.5D  + (random.nextFloat() - 0.5), (double)pos.getZ() + 0.5D + (random.nextFloat() - 0.5), (double)((random.nextFloat() - 0.5F) / 9.0F), (double)(random.nextFloat() / 6.0F), (double)((random.nextFloat() - 0.5) / 9.0F));
            }
        }
    }

    static {
        WATERLOGGED = Properties.WATERLOGGED;
        SHAPE = Block.createCuboidShape(5.0D, 0.0D, 2.0D, 11.0D, 4.0D, 14.0D);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

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
