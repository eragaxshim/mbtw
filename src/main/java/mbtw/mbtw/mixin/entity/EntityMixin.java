package mbtw.mbtw.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;

    @Shadow public boolean removed;

    @Shadow public abstract BlockPos getBlockPos();

    @Shadow @Nullable public abstract ItemEntity dropItem(ItemConvertible item, int yOffset);

    @Shadow @Final protected Random random;

    @Shadow @Final protected DataTracker dataTracker;

    @Shadow public float pitch;

    @Inject(method = "extinguish", at = @At("HEAD"))
    protected void changeExtinguish(CallbackInfo ci)
    {

    }
}
