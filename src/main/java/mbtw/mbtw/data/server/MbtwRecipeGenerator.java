package mbtw.mbtw.data.server;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.data.server.recipe.HopperRecipeJsonProvider;
import mbtw.mbtw.data.server.recipe.MechanicalRecipeJsonProvider;
import mbtw.mbtw.data.server.recipe.RecipeJsonBuilder;
import mbtw.mbtw.data.server.recipe.SpecialRecipeJsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class MbtwRecipeGenerator extends FabricRecipeProvider {
    public MbtwRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Mbtw.CRAFTING_STATION).input('#', ItemTags.PLANKS).input('@', Items.IRON_INGOT).pattern("@@@").pattern("#@#").pattern("@@@").criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);

        offerBrickSmelting(exporter, Mbtw.IRON_ORE_CHUNK, Items.IRON_NUGGET, 0,2000);
        offerBrickSmelting(exporter, Mbtw.CLAY_BRICK, Items.BRICK, 0,1000);
        offerMilling(exporter, Items.WHEAT, 1, Mbtw.FLOUR, 2, 10, 1);
        Identifier conversionId = SpecialRecipeJsonBuilder.create(Mbtw.HOPPER_BLOCK_SERIALIZER).offerTo(exporter, "hopper_conversion_urn");
        offerHopperFiltering(exporter, Items.NETHERRACK, Blocks.SOUL_SAND, 1, Mbtw.FLOUR, conversionId);
    }

    public static void offerMilling(Consumer<RecipeJsonProvider> exporter, ItemConvertible input, int inputCount, ItemConvertible output, int outputCount, int processingTime, int requiredPower) {
        MechanicalRecipeJsonProvider provider = new MechanicalRecipeJsonProvider(Ingredient.ofItems(input), inputCount, output.asItem(), outputCount, processingTime, requiredPower, Mbtw.MILLING_SERIALIZER);
        new RecipeJsonBuilder(RecipeCategory.MISC, provider).inputCriterion(input).offerTo(exporter, output, input);
    }

    public static void offerHopperFiltering(Consumer<RecipeJsonProvider> exporter, ItemConvertible input, Block filter, int inputCount, Item result, Identifier conversionRecipeId) {
        HopperRecipeJsonProvider provider = new HopperRecipeJsonProvider(Ingredient.ofItems(input), filter, inputCount, result, conversionRecipeId, Mbtw.HOPPER_SERIALIZER);
        new RecipeJsonBuilder(RecipeCategory.MISC, provider).inputCriterion(input).offerTo(exporter, result, input);
    }

    public static void offerBrickSmelting(Consumer<RecipeJsonProvider> exporter, ItemConvertible input, ItemConvertible output, float experience, int cookingTime) {
        criterionOfferCooking(input, RecipeProvider.hasItem(input), output, "_from_brick_smelting", createBrickSmeltingMisc(Ingredient.ofItems(input), output, experience, cookingTime), exporter);
    }

    public static void criterionOfferCooking(ItemConvertible input, String criterionName, ItemConvertible output, String method, CookingRecipeJsonBuilder builder, Consumer<RecipeJsonProvider> exporter) {
        builder.criterion(criterionName, VanillaRecipeProvider.conditionsFromItem(input)).offerTo(exporter, Mbtw.MOD_ID + ":" + RecipeProvider.getItemPath(output) + method + "_" + RecipeProvider.getItemPath(input));
    }

    public static CookingRecipeJsonBuilder createBrickSmeltingMisc(Ingredient input, ItemConvertible output, float experience, int cookingTime) {
        return new CookingRecipeJsonBuilder(RecipeCategory.MISC, CookingRecipeCategory.MISC, output, input, experience, cookingTime, Mbtw.BRICK_SMELTING_SERIALIZER);
    }

}
