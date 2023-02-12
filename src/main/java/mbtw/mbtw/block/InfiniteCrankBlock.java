package mbtw.mbtw.block;

import mbtw.mbtw.DynamicMechanicalSource;
import mbtw.mbtw.block.entity.MechanicalSourceBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfiniteCrankBlock extends Block implements MechanicalSource {
    public static BooleanProperty MODE = BooleanProperty.of("crank_mode");
    public static List<Direction> DIRECTIONS = List.of(Direction.values());

    public InfiniteCrankBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(MODE, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            world.setBlockState(pos, state.with(MODE, !state.get(MODE)), Block.NOTIFY_ALL);
            return ActionResult.CONSUME;
        }
    }

    @Override
    public boolean isSourceAtFace(BlockState state, Direction face) {
        return true;
    }

    @Override
    public int getSourceAtFace(BlockState state, Direction face) {
        return state.get(MODE) ? 4 : 2;
    }

    @Override
    public boolean getBearingAtFace(World world, BlockState state, BlockPos pos, @Nullable MechanicalSourceBlockEntity blockEntity, Direction face) {
        return true;
    }

    @Override
    public List<Direction> getOutputFaces(BlockState sourceState) {
        return DIRECTIONS;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(MODE);
    }
}
