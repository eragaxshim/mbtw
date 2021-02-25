package mbtw.mbtw.mixin;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.BreakInterceptable;
import mbtw.mbtw.block.InnerLogBlock;
import mbtw.mbtw.tag.MbtwTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

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

            if (isInterceptable || block.isIn(MbtwTags.BREAK_INTERCEPTABLES))
            {
                BlockState newState;
                if (isInterceptable) {
                    newState = ((BreakInterceptable) block).processBreakAttempt(world, pos, state, handStack);
                }
                else {
                    if (block == Blocks.STONE) {
                        newState = ((BreakInterceptable) Mbtw.MBTW_STONE).processBreakAttempt(world, pos, Mbtw.MBTW_STONE.getDefaultState(), handStack);
                    }
                    else if (block.isIn(BlockTags.LOGS)) {
                        BlockState possibleInnerLogState = InnerLogBlock.innerLogFromLog(state);
                        newState = possibleInnerLogState.getBlock() instanceof InnerLogBlock ? ((InnerLogBlock) possibleInnerLogState.getBlock()).processBreakAttempt(world, pos, possibleInnerLogState, handStack) : state;
                    }
                    else {
                        newState = state;
                    }
                }

                if (newState != state)
                {
                    Block.replace(state, newState, world, pos, 2);

                    BooleanProperty BROKEN = (BooleanProperty) newState.getBlock().getStateManager().getProperty("broken");
                    if (!newState.get(BROKEN)) {
                        cir.setReturnValue(true);
                    }
                    else {
                        newState.getBlock().onBreak(world, pos, newState, player);
                        player.addExhaustion(0.005F);
                    }
                }
            }
        }
    }
}

