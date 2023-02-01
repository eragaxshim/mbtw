package mbtw.mbtw.mixin.item;

import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;

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
