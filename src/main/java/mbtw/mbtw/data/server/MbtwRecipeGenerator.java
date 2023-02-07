package mbtw.mbtw.data.server;

import mbtw.mbtw.Mbtw;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class MbtwRecipeGenerator extends FabricRecipeProvider {
    public MbtwRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        offerBrickSmelting(exporter, Mbtw.IRON_ORE_CHUNK, Items.IRON_NUGGET, 0,2000);
        offerMilling(exporter, Items.WHEAT, Mbtw.FLOUR, 0, 10);
    }

    public static void offerBrickSmelting(Consumer<RecipeJsonProvider> exporter, ItemConvertible input, ItemConvertible output, float experience, int cookingTime) {
        criterionOffer(input, RecipeProvider.hasItem(input), output, "_from_brick_smelting", createBrickSmeltingMisc(Ingredient.ofItems(input), output, experience, cookingTime), exporter);
    }

    public static void offerMilling(Consumer<RecipeJsonProvider> exporter, ItemConvertible input, ItemConvertible output, float experience, int cookingTime) {
        criterionOffer(input, RecipeProvider.hasItem(input), output, "_from_milling", createMillingMisc(Ingredient.ofItems(input), output, experience, cookingTime), exporter);
    }

    public static void criterionOffer(ItemConvertible input, String criterionName, ItemConvertible output, String method, CookingRecipeJsonBuilder builder, Consumer<RecipeJsonProvider> exporter) {
        builder.criterion(criterionName, VanillaRecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.getItemPath(output) + method + "_" + RecipeProvider.getItemPath(input));
    }

    public static CookingRecipeJsonBuilder createBrickSmeltingMisc(Ingredient input, ItemConvertible output, float experience, int cookingTime) {
        return new CookingRecipeJsonBuilder(RecipeCategory.MISC, CookingRecipeCategory.MISC, output, input, experience, cookingTime, Mbtw.MILLING_SERIALIZER);
    }

    public static CookingRecipeJsonBuilder createMillingMisc(Ingredient input, ItemConvertible output, float experience, int cookingTime) {
        return new CookingRecipeJsonBuilder(RecipeCategory.MISC, CookingRecipeCategory.MISC, output, input, experience, cookingTime, Mbtw.MILLING_SERIALIZER);
    }

}
