package mbtw.mbtw.mixin.entity;

import mbtw.mbtw.item.Extinguishable;
import mbtw.mbtw.item.TickDamageItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends EntityMixin {

    @Shadow public abstract ItemStack getStack();

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

    @Override
    protected void changeExtinguish(CallbackInfo ci)
    {
        if (!this.world.isClient && this.getStack().getItem() instanceof Extinguishable)
        {
            ((Extinguishable) this.getStack().getItem()).extinguish(this.getStack(), this.world);
        }
    }
}
