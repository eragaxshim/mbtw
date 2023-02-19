package mbtw.mbtw.mixin.item;

import mbtw.mbtw.block.entity.MechanicalHopperBlockEntity;
import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Shadow public abstract Block getBlock();

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = context.getStack();
        if (!stack.isIn(MbtwTagsMaps.SOUL_FILTERS)) {
            return;
        }

        BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
        if (blockEntity instanceof MechanicalHopperBlockEntity hopper) {
            if (hopper.getFilter() == null || hopper.getFilter() == Blocks.AIR) {
                hopper.setFilter(this.getBlock());
            } else {
                hopper.setFilter(Blocks.AIR);
            }

            cir.setReturnValue(ActionResult.CONSUME);
        }
    }
}
