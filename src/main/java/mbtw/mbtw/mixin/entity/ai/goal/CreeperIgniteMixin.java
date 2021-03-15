package mbtw.mbtw.mixin.entity.ai.goal;

import mbtw.mbtw.entity.CreeperMixinAccess;
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperIgniteGoal.class)
public class CreeperIgniteMixin {
    @Shadow @Final private CreeperEntity creeper;

    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    protected void changeCanStart(CallbackInfoReturnable<Boolean> cir)
    {
        if (((CreeperMixinAccess) this.creeper).getDefused())
        {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"), cancellable = true)
    protected void changeCanStart(CallbackInfo ci)
    {
        if (((CreeperMixinAccess) this.creeper).getDefused())
        {
            this.creeper.setFuseSpeed(-1);
        }
    }
}
