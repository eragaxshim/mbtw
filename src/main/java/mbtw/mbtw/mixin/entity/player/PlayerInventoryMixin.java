package mbtw.mbtw.mixin.entity.player;

import mbtw.mbtw.screen.ScreenHandlerMixinAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Shadow private ItemStack cursorStack;

    @Shadow @Final public PlayerEntity player;

    @Inject(method = "updateItems", at = @At("HEAD"))
    protected void changeUpdateItems(CallbackInfo ci)
    {
        if (!this.cursorStack.isEmpty())
        {
            this.cursorStack.inventoryTick(this.player.world, this.player, -1, false);
        }
    }
}
