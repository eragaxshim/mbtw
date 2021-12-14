package mbtw.mbtw.mixin.entity;

import mbtw.mbtw.item.ItemTickable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StorageMinecartEntity.class)
public abstract class StorageMinecartEntityMixin extends AbstractMinecartEntityMixin {
    @Shadow private DefaultedList<ItemStack> inventory;

    public StorageMinecartEntityMixin(EntityType<?> type, World world) { super(type, world); }

    @Override
    protected void changeTick(CallbackInfo ci)
    {
        if (!this.isRemoved() && this.world.getTime() % 23 == 0)
        {
            for (ItemStack stack : this.inventory)
            {
                if (stack.getItem() instanceof ItemTickable)
                {
                    ((ItemTickable)stack.getItem()).tick(stack, this.world, this.getBlockPos(), null);
                }
            }
        }
    }
}
