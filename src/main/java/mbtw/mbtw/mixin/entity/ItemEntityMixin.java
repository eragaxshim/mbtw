package mbtw.mbtw.mixin.entity;

import mbtw.mbtw.item.TickDamageItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Shadow public abstract ItemStack getStack();

    public ItemEntityMixin(EntityType<?> type, World world) { super(type, world); }

    @Inject(method = "tick", at = @At("TAIL"))
    protected void itemTick(CallbackInfo ci)
    {
        if (!this.removed && this.world.getTime() % 23 == 0)
        {
            ItemStack stack = this.getStack();
            if (stack.getItem() instanceof TickDamageItem)
            {
                ((TickDamageItem)stack.getItem()).tick(stack, this.world, this.getBlockPos());
            }
        }
    }
}
