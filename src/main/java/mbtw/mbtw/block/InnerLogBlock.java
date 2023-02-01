package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

public class InnerLogBlock extends PillarBlock implements BreakInterceptable {
    protected static final VoxelShape SHAPE_B1;
    protected static final VoxelShape SHAPE_B1_X;
    protected static final VoxelShape SHAPE_B1_Z;
    protected static final VoxelShape SHAPE_B2;
    protected static final VoxelShape SHAPE_B2_X;
    protected static final VoxelShape SHAPE_B2_Z;
    protected static final VoxelShape SHAPE_B3;
    protected static final VoxelShape SHAPE_B3_X;
    protected static final VoxelShape SHAPE_B3_Z;
    protected static final VoxelShape SHAPE_B4;
    protected static final VoxelShape SHAPE_B4_X;
    protected static final VoxelShape SHAPE_B4_Z;
    public static final IntProperty BREAK_LEVEL = IntProperty.of("break_level", 0, 5);
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;

    public InnerLogBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(BreakInterceptable.BROKEN, false).with(PillarBlock.AXIS, Direction.Axis.Y).with(BREAK_LEVEL, 0).with(UP, true).with(DOWN, true));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int b = state.get(BREAK_LEVEL);
        boolean connected = state.get(UP) && state.get(DOWN);
        if (b == 0 || b == 5)
        {
            return VoxelShapes.fullCube();
        }
        else if (connected || b == 1)
        {
            return switch (state.get(PillarBlock.AXIS)) {
                case X -> SHAPE_B1_X;
                default -> SHAPE_B1;
                case Z -> SHAPE_B1_Z;
            };
        }
        else {
            return switch (b) {
                case 2 -> switch (state.get(PillarBlock.AXIS)) {
                    case X -> SHAPE_B2_X;
                    default -> SHAPE_B2;
                    case Z -> SHAPE_B2_Z;
                };
                case 3 -> switch (state.get(PillarBlock.AXIS)) {
                    case X -> SHAPE_B3_X;
                    default -> SHAPE_B3;
                    case Z -> SHAPE_B3_Z;
                };
                case 4 -> switch (state.get(PillarBlock.AXIS)) {
                    case X -> SHAPE_B4_X;
                    default -> SHAPE_B4;
                    case Z -> SHAPE_B4_Z;
                };
                default -> VoxelShapes.fullCube();
            };
        }
    }

    public BlockState processBreakAttempt(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack handStack)
    {
        int b = state.get(BREAK_LEVEL);

        int d = state.get(PillarBlock.AXIS) == Direction.Axis.Z ? -1 : 1;
        if (state.get(UP) && !world.getBlockState(pos.offset(state.get(PillarBlock.AXIS), d)).isIn(BlockTags.LOGS))
//                !BlockTags.LOGS.
//                .contains())
        {
            state = state.with(UP, false);
        }
        if (state.get(DOWN) && world.getBlockState(pos.offset(state.get(PillarBlock.AXIS), -d)).isIn(BlockTags.LOGS))
        {
            state = state.with(DOWN, false);
        }

        if (handStack.getItem() instanceof AxeItem && (!(this instanceof InnerTrunkBlock) || ((AxeItem) handStack.getItem()).getMaterial().getMiningLevel() > 3)) {
            return state.with(BreakInterceptable.BROKEN, true);
        }

        if (b == 5)
        {
            b = 0;
        }

        if (b+1 > 4)
        {
            return state.with(BreakInterceptable.BROKEN, true);
        }
        else {
            Block.dropStacks(state, world, pos, world.getBlockEntity(pos), player, handStack);
            return state.with(BREAK_LEVEL, b+1);
        }
    }

    static {
        Double[] coordsB1 = {1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D};
        Double[] coordsB2 = {2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D};
        Double[] coordsB3 = {3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D};
        Double[] coordsB4 = {4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D};

        //TODO
        SHAPE_B1 = createCuboidShapeFromArray(coordsB1);
        SHAPE_B1_X = createCuboidShapeFromArray(coordsB1);
        SHAPE_B1_Z = createCuboidShapeFromArray(coordsB1);
//        SHAPE_B1_X = createCuboidShapeFromArray(rotateCuboidCoords(coordsB1, 0));
//        SHAPE_B1_Z = createCuboidShapeFromArray(rotateCuboidCoords(coordsB1, 1));
        SHAPE_B2 = createCuboidShapeFromArray(coordsB2);
        SHAPE_B2_X = createCuboidShapeFromArray(coordsB2);
        SHAPE_B2_Z = createCuboidShapeFromArray(coordsB2);
//        SHAPE_B2_X = createCuboidShapeFromArray(rotateCuboidCoords(coordsB2, 0));
//        SHAPE_B2_Z = createCuboidShapeFromArray(rotateCuboidCoords(coordsB2, 1));
        SHAPE_B3 = createCuboidShapeFromArray(coordsB3);
        SHAPE_B3_X = createCuboidShapeFromArray(coordsB3);
        SHAPE_B3_Z = createCuboidShapeFromArray(coordsB3);
//        SHAPE_B3_X = createCuboidShapeFromArray(rotateCuboidCoords(coordsB3, 0));
//        SHAPE_B3_Z = createCuboidShapeFromArray(rotateCuboidCoords(coordsB3, 1));
        SHAPE_B4 = createCuboidShapeFromArray(coordsB4);
        SHAPE_B4_X = createCuboidShapeFromArray(coordsB4);
        SHAPE_B4_Z = createCuboidShapeFromArray(coordsB4);
//        SHAPE_B4_X = createCuboidShapeFromArray(rotateCuboidCoords(coordsB4, 0));
//        SHAPE_B4_Z = createCuboidShapeFromArray(rotateCuboidCoords(coordsB4, 1));
        UP = Properties.UP;
        DOWN = Properties.DOWN;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BreakInterceptable.BROKEN);
        stateManager.add(PillarBlock.AXIS);
        stateManager.add(BREAK_LEVEL);
        stateManager.add(UP);
        stateManager.add(DOWN);
    }

    private static VoxelShape createCuboidShapeFromArray(Double[] coords)
    {
        return Block.createCuboidShape(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
    }

    private static Double[] rotateCuboidCoords(Double[] coords, int rotation)
    {
        Double[] newMinCoords = rotateCoordinate(coords[0], coords[1], coords[2], rotation);
        Double[] newMaxCoords = rotateCoordinate(coords[3], coords[4], coords[5], rotation);

        return ArrayUtils.addAll(newMinCoords, newMaxCoords);
    }

    private static Double[] rotateCoordinate(double x, double y, double z, int rotation)
    {
        // from Y to X
        if (rotation == 0)
        {
            return new Double[] { y, 16.0D - x, z };
        }
        // from Y to Z
        else if (rotation == 1) {
            return new Double[] { x, 16.0D - z, y};
        }
        else {
            return new Double[] { x, y, z };
        }
    }
}
