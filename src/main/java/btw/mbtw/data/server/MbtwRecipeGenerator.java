package btw.mbtw.data.server;

import btw.mbtw.Mbtw;
import btw.mbtw.recipe.BrickOvenRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MbtwRecipeGenerator extends FabricRecipeProvider {

    public static void offerBrickSmelting(
            RecipeExporter exporter, List<ItemConvertible> inputs, ItemConvertible output, float experience, int cookingTime, String group
    ) {
        for (ItemConvertible itemConvertible : inputs) {
            CookingRecipeJsonBuilder builder = new CookingRecipeJsonBuilder(RecipeCategory.MISC, CookingRecipeCategory.MISC, output, Ingredient.ofItems(itemConvertible), experience, cookingTime, BrickOvenRecipe::new);

            builder.group(group)
                    .criterion(hasItem(itemConvertible), conditionsFromItem(itemConvertible))
                    .offerTo(exporter, getItemPath(output) + "_from_brick_smelting_" + getItemPath(itemConvertible));
        }
    }


    public MbtwRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        offerBrickSmelting(exporter, List.of(Items.CLAY_BALL), Items.BRICK, 0, 100, "brick");
    }
}

