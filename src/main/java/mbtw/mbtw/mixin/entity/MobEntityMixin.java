package mbtw.mbtw.mixin.entity;

import mbtw.mbtw.item.Extinguishable;
import mbtw.mbtw.item.ItemTickable;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntityMixin {
    @Shadow
    @Final
    protected GoalSelector goalSelector;

    @Shadow @Final private DefaultedList<ItemStack> handItems;

    @Shadow @Final private DefaultedList<ItemStack> armorItems;

    @Inject(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;remove()V"))
    public void createCobwebAtDespawn(CallbackInfo ci)
    {

    }

    @Inject(method = "onEatingGrass", at = @At("HEAD"))
    protected void changeEatinGrass(CallbackInfo ci) {

    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    protected void changeInteraction(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir)
    {

    }

    @Inject(method = "tick", at = @At("TAIL"))
    protected void changeMobTick(CallbackInfo ci)
    {
        if (!this.world.isClient && this.world.getTime() % 23 == 0)
        {
            DefaultedList<ItemStack> combinedStacks = DefaultedList.of();
            combinedStacks.addAll(this.handItems);
            combinedStacks.addAll(this.armorItems);
            for (ItemStack stack : combinedStacks)
            {
                if (stack.getItem() instanceof ItemTickable)
                {
                    ((ItemTickable)stack.getItem()).tick(stack, this.world, this.getBlockPos(), (MobEntity) (Object) this);
                }
            }
        }
    }

    @Override
    protected void changeExtinguish(CallbackInfo ci)
    {
        if (!this.world.isClient && this.isTouchingWater())
        {
            for (ItemStack stack : this.handItems)
            {
                if (stack.getItem() instanceof Extinguishable)
                {
                    ((Extinguishable) stack.getItem()).extinguish(stack, this.world, this.getBlockPos());
                }
            }
        }
    }
}
