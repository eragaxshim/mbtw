package mbtw.mbtw.mixin.block;

import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin {
    @Mutable
    @Shadow @Final private DefaultedList<ItemStack> itemsBeingCooked;

    @Mutable
    @Shadow @Final private int[] cookingTimes;

    @Mutable
    @Shadow @Final private int[] cookingTotalTimes;

    @Inject(method = "<init>", at = @At("TAIL"))
    protected void changeMaxItems(CallbackInfo ci)
    {
        this.itemsBeingCooked = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.cookingTimes = new int[2];
        this.cookingTotalTimes = new int[2];
    }
}
