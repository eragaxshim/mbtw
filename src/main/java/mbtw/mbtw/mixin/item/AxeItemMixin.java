package mbtw.mbtw.mixin.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin extends ItemEffectiveMixin {
    @Shadow @Final private static Set<Material> field_23139;

    @Shadow @Final private static Set<Block> EFFECTIVE_BLOCKS;

    @Override
    protected void changeEffectiveOn(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (field_23139.contains(state.getMaterial()) || EFFECTIVE_BLOCKS.contains(state.getBlock()))
        {
            cir.setReturnValue(true);
        }
        else {
            System.out.println("huh");
        }
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    protected void disableAxeStrip(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        cir.setReturnValue(ActionResult.PASS);
    }
}
