package mbtw.mbtw.mixin.entity.passive;

import mbtw.mbtw.item.ItemTickable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseBaseEntity.class)
public abstract class HorseBaseEntityMixin extends Entity {
    @Shadow protected SimpleInventory items;

    public HorseBaseEntityMixin(EntityType<?> type, World world) { super(type, world); }

    @Inject(method = "tick", at = @At("TAIL"))
    protected void itemTick(CallbackInfo ci)
    {
        if (!this.removed && this.world.getTime() % 23 == 0)
        {
            for (int i = 0; i < this.items.size(); i++)
            {
                ItemStack stack = this.items.getStack(i);
                if (stack.getItem() instanceof ItemTickable)
                {
                    ((ItemTickable)stack.getItem()).tick(stack, this.world, this.getBlockPos(), null);
                }
            }
        }
    }
}
