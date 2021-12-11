package mbtw.mbtw.mixin.block;

import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class BlockBreakingDeltaMixin {
    @Inject(method = "calcBlockBreakingDelta", at = @At("RETURN"), cancellable = true)
    protected void changeBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir)
    {
        float calculatedDelta = cir.getReturnValue();

        if (calculatedDelta > 0.0f)
        {
            // non-effective / 200 vs effective / 80 (i.e. tool is 2.5 times faster)
            float newDelta = calculatedDelta / (player.canHarvest(state) ? 60.0F / 30.0F : 200.0F / 100.0F);
            boolean usingEffective = player.inventory.getMainHandStack().isSuitableFor(state);
            Block block = state.getBlock();

            if (!usingEffective)
            {
                newDelta /= 1.25F;
                if (block.isIn(MbtwTagsMaps.EASY_HAND_BREAKABLES))
                {
                    newDelta *= 3.0F;
                }
                else if (block.isIn(MbtwTagsMaps.HAND_UNBREAKABLES))
                {
                    newDelta /= 2.0F;
                }
                else if (MbtwTagsMaps.HAND_BREAKABLE_MATERIALS.contains(state.getMaterial()))
                {
                    newDelta *= 2.0F;
                }
            }
            else if (block.isIn(MbtwTagsMaps.TOOL_REDUCED_EFFECTIVENESS)) {
                newDelta /= 1.5F;
            }

            cir.setReturnValue(newDelta);
        }
    }
}
