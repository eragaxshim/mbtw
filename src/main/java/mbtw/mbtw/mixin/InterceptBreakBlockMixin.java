package mbtw.mbtw.mixin;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.BreakInterceptable;
import mbtw.mbtw.block.InnerLogBlock;
import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class InterceptBreakBlockMixin {
    @Shadow public ServerWorld world;

    @Shadow public ServerPlayerEntity player;

    @Shadow public abstract boolean isCreative();

    @Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    protected void interceptBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState state, BlockEntity entity, Block block)
    {
        if (!this.isCreative())
        {
            ServerWorld world = this.world;
            ServerPlayerEntity player = this.player;
            ItemStack handStack = player.getMainHandStack();
            boolean isInterceptable = block instanceof BreakInterceptable;

            if (isInterceptable || block.isIn(MbtwTagsMaps.BREAK_INTERCEPTABLES))
            {
                BlockState newState = state;
                if (isInterceptable) {
                    newState = ((BreakInterceptable) block).processBreakAttempt(world, pos, state, player, handStack);
                }
                else {
                    if (block == Blocks.STONE) {
                        newState = ((BreakInterceptable) Mbtw.STONE).processBreakAttempt(world, pos, Mbtw.STONE.getDefaultState(), player, handStack);
                    }
                    else if (block.isIn(BlockTags.LOGS)) {
                        BlockState possibleInnerLogState = MbtwTagsMaps.INNER_LOG_MAP.get(block);
                        if (possibleInnerLogState.getBlock() instanceof InnerLogBlock)
                        {
                            newState = ((InnerLogBlock) possibleInnerLogState.getBlock()).processBreakAttempt(world, pos, possibleInnerLogState.with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)), player, handStack);
                        }
                    }
                }

                if (newState != state)
                {
                    Block.replace(state, newState, world, pos, 2);

                    if (!newState.get(BreakInterceptable.BROKEN)) {
                        handStack.postMine(this.world, newState, pos, this.player);
                        cir.setReturnValue(true);
                    }
                    else {
                        newState.getBlock().onBreak(world, pos, newState, player);
                    }
                }
            }
        }
    }
}

