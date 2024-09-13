package btw.mbtw.recipe;

import btw.mbtw.Mbtw;
import btw.mbtw.block.MbtwBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.util.Identifier;

public class BrickOvenRecipe extends AbstractCookingRecipe {
    public BrickOvenRecipe(String group, CookingRecipeCategory category, Ingredient input, ItemStack output, float experience, int cookTime) {
        super(Mbtw.BRICK_SMELTING, group, category, input, output, experience, cookTime);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(MbtwBlocks.BRICK_OVEN);
    }

    public RecipeSerializer<?> getSerializer() {
        return Mbtw.BRICK_SMELTING_SERIALIZER;
    }
}
