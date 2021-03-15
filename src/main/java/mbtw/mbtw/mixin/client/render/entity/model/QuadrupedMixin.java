package mbtw.mbtw.mixin.client.render.entity.model;

import mbtw.mbtw.entity.QuadrupedMixinAccess;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QuadrupedEntityModel.class)
public abstract class QuadrupedMixin<T extends Entity> extends EntityModelMixin<T> {
    @Shadow protected ModelPart head;
    private float headPitchModifier;

    @Inject(method = "setAngles", at = @At("TAIL"))
    protected void changeAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci)
    {
        if (entity instanceof CowEntity)
        {
            this.head.pitch = this.headPitchModifier;
        }
    }

    @Override
    protected void changeAnimateModel(T entity, float limbAngle, float limbDistance, float tickDelta, CallbackInfo ci) {
        super.changeAnimateModel(entity, limbAngle, limbDistance, tickDelta, ci);
        if (entity instanceof CowEntity)
        {
            System.out.println("changeAnimateQQ");
            this.head.pivotY = 6.0F + ((QuadrupedMixinAccess) entity).getNeckAngle(tickDelta) * 9.0F;
            this.headPitchModifier = ((QuadrupedMixinAccess) entity).getHeadAngle(tickDelta);
        }
    }
}
