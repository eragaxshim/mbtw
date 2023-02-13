package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.entity.CrucibleBlockEntity;
import mbtw.mbtw.block.entity.MillstoneBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrucibleBlock extends Block implements BlockEntityProvider {
    public CrucibleBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != Mbtw.CRUCIBLE_ENTITY || world.isClient) {
            return null;
        }

        return (world1, pos, state1, millstone) -> CrucibleBlockEntity.serverTick(world1, pos, state1, (CrucibleBlockEntity) millstone);
    }

    protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CrucibleBlockEntity) {
            player.openHandledScreen((NamedScreenHandlerFactory) blockEntity);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        this.openScreen(world, pos, player);
        return ActionResult.CONSUME;
    }
}
