package mbtw.mbtw.mixin.entity.passive;

import mbtw.mbtw.mixin.entity.MobEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class AnimalMixin extends MobEntityMixin {
    protected AnimalMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "mobTick", at = @At("HEAD"))
    protected void changeMobTick(CallbackInfo ci)
    {

    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    protected void changeTickMovement(CallbackInfo ci)
    {

    }
}
