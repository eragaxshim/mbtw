package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.entity.ClayBrickBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.random.Random;

public class ClayBrickBlock extends BlockWithEntity implements Waterloggable, BlockEntityProvider {
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;
    public static final IntProperty BAKE_PROGRESS = IntProperty.of("bake_progress", 0, 8);

    public ClayBrickBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(WATERLOGGED, false).with(BAKE_PROGRESS, 0));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ClayBrickBlockEntity(pos, state);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        long time = world.getTimeOfDay();
        if (state.get(BAKE_PROGRESS) != 8 && (time < 12500 || time > 23500) && world.isSkyVisible(pos) && !world.isRaining() && !state.get(WATERLOGGED)) {
            for(int i = 0; i < random.nextInt(2) + 1; ++i) {
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
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return direction == Direction.DOWN && !this.canPlaceAt(state, world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return sideCoversSmallSquare(world, pos.down(), Direction.UP);
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        this.tryBreakBrick(world, pos, entity);
        super.onSteppedOn(world, pos, state, entity);
    }

    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        this.tryBreakBrick(world, pos, entity);
        super.onLandedUpon(world, state, pos, entity, fallDistance);
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

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        BlockState superState = super.getPlacementState(ctx);
        return superState != null ? superState.with(WATERLOGGED, bl) : null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, Mbtw.CLAY_BRICK_ENTITY, (world1, pos, state1, be) -> ClayBrickBlockEntity.tick(world1, pos, state1, be));
    }
}
