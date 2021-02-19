package mbtw.mbtw.recipe;

import mbtw.mbtw.Mbtw;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class BrickOvenRecipe extends AbstractCookingRecipe {
    public BrickOvenRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
        super(Mbtw.BRICK_SMELTING, id, group, input, output, experience, cookTime);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(Mbtw.BRICK_OVEN);
    }

    public RecipeSerializer<?> getSerializer() {
        return Mbtw.BRICK_SMELTING_SERIALIZER;
    }
}
