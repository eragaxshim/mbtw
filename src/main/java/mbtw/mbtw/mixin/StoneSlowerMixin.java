package mbtw.mbtw.mixin;

import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolMaterials.class)
public class StoneSlowerMixin {
    @Shadow @Final private int miningLevel;

    @Inject(method = "getMiningSpeedMultiplier", at = @At("RETURN"), cancellable = true)
    protected void modifyStoneMiningSpeed(CallbackInfoReturnable<Float> cir)
    {
        if (this.miningLevel == 1) {
            cir.setReturnValue(cir.getReturnValue() * 0.75F);
        }
    }
}
