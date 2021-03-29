package mbtw.mbtw.mixin.entity.player;

import mbtw.mbtw.item.Extinguishable;
import mbtw.mbtw.mixin.entity.EntityMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends EntityMixin {
    @Shadow @Final public PlayerInventory inventory;

    @Override
    protected void changeExtinguish(CallbackInfo ci)
    {
        if (!this.world.isClient && this.isTouchingWater())
        {
            for (int i = 0; i < this.inventory.size(); i++)
            {
                ItemStack stack = this.inventory.getStack(i);
                if (stack.getItem() instanceof Extinguishable)
                {
                    ((Extinguishable) stack.getItem()).extinguish(stack, this.world, this.getBlockPos());
                }
            }
        }
    }
}
