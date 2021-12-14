package mbtw.mbtw.mixin.screen.slot;

import mbtw.mbtw.recipe.RecipeMixinAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

@Mixin(CraftingResultSlot.class)
public class CraftingResultMixin {

    @Shadow @Final private CraftingInventory input;

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    protected void dropOutputResult(PlayerEntity player, ItemStack stack, CallbackInfo ci)
    {
        MinecraftServer server = player.world.getServer();
        if (server != null)
        {
            Optional<CraftingRecipe> optional = server.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, this.input, player.world);
            CraftingRecipe craftingRecipe;
            if (optional.isPresent() && (craftingRecipe = optional.get()) instanceof ShapelessRecipe) {

                DefaultedList<ItemStack> dropOutput = ((RecipeMixinAccess)craftingRecipe).getDropOutput();
                if (!dropOutput.isEmpty())
                {
                    for (ItemStack itemStack : dropOutput)
                    {
                        player.dropStack(itemStack.copy());
                    }
                }
            }
        }
    }
}
