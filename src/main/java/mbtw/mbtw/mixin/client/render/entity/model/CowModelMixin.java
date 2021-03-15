package mbtw.mbtw.mixin.client.render.entity.model;

import mbtw.mbtw.entity.QuadrupedMixinAccess;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.CowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CowEntityModel.class)
public abstract class CowModelMixin<T extends CowEntity> extends QuadrupedMixin<T> {

}
