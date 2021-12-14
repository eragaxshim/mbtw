package mbtw.mbtw.mixin.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ShovelItem.class)
public class ShovelEffectiveMixin {
//    @Shadow @Final private static Set<Block> EFFECTIVE_BLOCKS;
//
//    @Inject(method = "isEffectiveOn", at = @At("HEAD"), cancellable = true)
//    protected void changeEffectiveOn(BlockState state, CallbackInfoReturnable<Boolean> cir)
//    {
//        if (EFFECTIVE_BLOCKS.contains(state.getBlock()))
//        {
//            cir.setReturnValue(true);
//        }
//    }
}
