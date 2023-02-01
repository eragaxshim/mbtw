package mbtw.mbtw.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;

    @Shadow public abstract BlockPos getBlockPos();

    @Shadow @Nullable public abstract ItemEntity dropItem(ItemConvertible item, int yOffset);

    @Shadow @Final protected Random random;

    @Shadow @Final protected DataTracker dataTracker;

    @Shadow
    private float pitch;

    @Shadow public abstract boolean isTouchingWater();

    @Shadow public abstract boolean isRemoved();

    @Inject(method = "checkWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;extinguish()V"))
    protected void changeExtinguish(CallbackInfo ci)
    {

    }

    public float getPitch() {
        return pitch;
    }
}
