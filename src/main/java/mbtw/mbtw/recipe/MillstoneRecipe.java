package mbtw.mbtw.recipe;

import mbtw.mbtw.Mbtw;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.util.Identifier;

public class MillstoneRecipe extends AbstractMechanicalRecipe {
    public MillstoneRecipe(Identifier id, String group, Ingredient input, int inputCount, ItemStack output, int processingTime, int requiredPower) {
        super(Mbtw.MILLING, id, group, input, inputCount, output, processingTime, requiredPower);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(Mbtw.MILLSTONE);
    }

    public RecipeSerializer<?> getSerializer() {
        return Mbtw.MILLING_SERIALIZER;
    }
}
