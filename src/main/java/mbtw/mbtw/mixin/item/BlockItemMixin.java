package mbtw.mbtw.mixin.item;

import mbtw.mbtw.block.entity.MechanicalHopperBlockEntity;
import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!context.getStack().isIn(MbtwTagsMaps.SOUL_FILTERS)) {
            return;
        }

        BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
        if (blockEntity instanceof MechanicalHopperBlockEntity hopper) {
            hopper.setFilter();
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }
}
