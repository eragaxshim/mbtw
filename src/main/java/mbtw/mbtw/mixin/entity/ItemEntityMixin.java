package mbtw.mbtw.mixin.entity;

import mbtw.mbtw.item.Extinguishable;
import mbtw.mbtw.item.ItemTickable;
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
        if (!this.isRemoved() && this.world.getTime() % 23 == 0)
        {
            ItemStack stack = this.getStack();
            if (stack.getItem() instanceof ItemTickable)
            {
                ((ItemTickable)stack.getItem()).tick(stack, this.world, this.getBlockPos(), (ItemEntity) (Object) this);
            }
        }
    }

    @Override
    protected void changeExtinguish(CallbackInfo ci)
    {
        if (!this.world.isClient && this.isTouchingWater() && this.getStack().getItem() instanceof Extinguishable)
        {
            ((Extinguishable) this.getStack().getItem()).extinguish(this.getStack(), this.world, this.getBlockPos());
        }
    }
}
