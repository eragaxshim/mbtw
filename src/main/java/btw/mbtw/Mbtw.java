package btw.mbtw;

import btw.mbtw.block.MbtwBlocks;
import btw.mbtw.block.entity.BrickOvenBlockEntity;
import btw.mbtw.recipe.BrickOvenRecipe;
import btw.mbtw.screen.BrickOvenScreenHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mbtw implements ModInitializer {
	public static final String MOD_ID = "mbtw";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static RecipeType<BrickOvenRecipe> BRICK_SMELTING;

	public static RecipeSerializer<BrickOvenRecipe> BRICK_SMELTING_SERIALIZER;
	public static final ScreenHandlerType<BrickOvenScreenHandler> BRICK_OVEN_SCREEN_HANDLER = new ScreenHandlerType<>(BrickOvenScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

	public static final RegistryKey<ItemGroup> MBTW_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(MOD_ID, "item_group"));
	public static final ItemGroup MBTW_ITEM_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(Items.BRICK))
			.displayName(Text.translatable("itemGroup.mbtw"))
			.build();

	static {
		BRICK_SMELTING_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(MOD_ID, "brick_smelting"), new CookingRecipeSerializer<>(BrickOvenRecipe::new, 100));
		BRICK_SMELTING = Registry.register(Registries.RECIPE_TYPE, Identifier.of(MOD_ID, "brick_smelting"), new RecipeType<BrickOvenRecipe>() {
			@Override
			public String toString() {
				return "brick_smelting";
			}
		});
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		MbtwBlocks.initialize();

		Registry.register(Registries.ITEM_GROUP, MBTW_ITEM_GROUP_KEY, MBTW_ITEM_GROUP);
		Registry.register(Registries.SCREEN_HANDLER, Identifier.of(MOD_ID, "brick_oven"), BRICK_OVEN_SCREEN_HANDLER);
	}
}