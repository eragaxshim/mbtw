package mbtw.mbtw.mixin.client.render.entity.model;

import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.CowEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CowEntityModel.class)
public abstract class CowModelMixin<T extends CowEntity> extends QuadrupedMixin<T> {

}
