package mbtw.mbtw.item;

import mbtw.mbtw.mixin.entity.LivingEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ConsumeDamageItem extends Item {
    private final ItemStack targetItem;
    private final boolean doesDamageDecrease;

    public ConsumeDamageItem(Settings settings, int useTime, ItemStack targetItem, boolean doesDamageDecrease) {
        super(settings.maxDamage(useTime));
        this.targetItem = targetItem;
        this.doesDamageDecrease = doesDamageDecrease;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        System.out.println("b" + stack.getDamage());

        return this.targetItem;
    }

    private static void spawnUseEffects(World world, LivingEntity user, ItemStack stack)
    {
        ((LivingEntityAccessor)user).invokeSpawnItemParticles(stack, 5);
        user.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5F, world.random.nextFloat() * 0.1F + 3.9F);
    }

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        user.swingHand(Hand.MAIN_HAND);
        if (remainingUseTicks % 4 == 0)
        {
            spawnUseEffects(world, user, stack);
        }
        if (!(user instanceof PlayerEntity) || !((PlayerEntity)user).abilities.creativeMode) {
            if (stack.getDamage() == (this.doesDamageDecrease ? 1 : this.getMaxDamage() - 1))
            {
                ((LivingEntityAccessor)user).setItemUseTimeLeft(1);
            }
            else if (stack.getDamage() != 0)
            {
                stack.setDamage(stack.getDamage() + (this.doesDamageDecrease ? -1 : 1));
            }
            else {
                stack.setDamage(this.doesDamageDecrease ? this.getMaxDamage() - 1 : 1);
            }
        }
    }

    public int getMaxUseTime(ItemStack stack) {
        return this.getMaxDamage() + 1;
    }

    public UseAction getUseAction(ItemStack stack) {
        return stack.getItem().isFood() ? UseAction.EAT : UseAction.NONE;
    }
}
