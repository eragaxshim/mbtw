package mbtw.mbtw.mixin.item;

import mbtw.mbtw.Mbtw;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {
    @Inject(method = "postMine", at = @At("RETURN"), cancellable = true)
    protected void changePostMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> cir)
    {
        if (!cir.getReturnValue() && state.isOf(Mbtw.DAMAGED_COBWEB))
        {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getMiningSpeedMultiplier", at = @At("RETURN"), cancellable = true)
    protected void changeMiningSpeedMultiplier(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir)
    {
        if (state.isOf(Mbtw.DAMAGED_COBWEB))
        {
            cir.setReturnValue(15.0F);
        }
    }

    @Inject(method = "isEffectiveOn", at = @At("HEAD"), cancellable = true)
    protected void changeEffectiveOn(BlockState state, CallbackInfoReturnable<Boolean> cir)
    {
        if (state.isOf(Mbtw.DAMAGED_COBWEB))
        {
            cir.setReturnValue(true);
        }
    }
}
