package mbtw.mbtw.mixin.client.render.entity.model;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityModel.class)
public class EntityModelMixin<T extends Entity> {

    @Inject(method = "animateModel", at = @At("TAIL"))
    protected void changeAnimateModel(T cowEntity, float limbAngle, float limbDistance, float tickDelta, CallbackInfo ci)
    {

    }
}
