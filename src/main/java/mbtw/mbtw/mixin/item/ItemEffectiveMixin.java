package mbtw.mbtw.mixin.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemEffectiveMixin {
    @Inject(method = "isEffectiveOn", at = @At("HEAD"), cancellable = true)
    protected void changeEffectiveOn(BlockState state, CallbackInfoReturnable<Boolean> cir) {

    }
}
