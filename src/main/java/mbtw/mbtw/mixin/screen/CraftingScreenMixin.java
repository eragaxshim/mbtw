package mbtw.mbtw.mixin.screen;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenMixin {
    private DefaultedList<ItemStack> dropOutput;

//    @ModifyVariable(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/CraftingRecipe;craft(Lnet/minecraft/inventory/Inventory;)Lnet/minecraft/item/ItemStack;"), ordinal = 0)
//    private static CraftingRecipe dropOutput(CraftingRecipe craftingRecipe, int syncId, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory)
//    {
//        return craftingRecipe;
//    }
}
