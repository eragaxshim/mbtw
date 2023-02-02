package mbtw.mbtw;

import mbtw.mbtw.block.*;
import mbtw.mbtw.block.entity.*;
import mbtw.mbtw.item.ChiselItem;
import mbtw.mbtw.item.FiniteTorchItem;
import mbtw.mbtw.item.FireStarterItem;
import mbtw.mbtw.loot.MbtwLootModifier;
import mbtw.mbtw.mixin.block.LitStateInvoker;
import mbtw.mbtw.recipe.BrickOvenRecipe;
import mbtw.mbtw.screen.BrickOvenScreenHandler;
import mbtw.mbtw.screen.TrunkWorkbenchScreenHandler;
import mbtw.mbtw.tag.MbtwTagsMaps;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Mbtw implements ModInitializer {
	public static final String MOD_ID = "mbtw";

	public static int DEEP_STONE_MAX = 25;
	public static int HARD_STONE_MAX = 45;

	public static final Item MBTW_EMPTY = new Item(new FabricItemSettings());
	public static final Item LOOSE_STONE = new Item(new FabricItemSettings());
	public static final Item SAW_DUST = new Item(new FabricItemSettings());
	public static final Item FUNGAL_DUST = new Item(new FabricItemSettings());
	public static final Item OAK_BARK = new Item(new FabricItemSettings());
	public static final Item SPRUCE_BARK = new Item(new FabricItemSettings());
	public static final Item BIRCH_BARK = new Item(new FabricItemSettings());
	public static final Item JUNGLE_BARK = new Item(new FabricItemSettings());
	public static final Item ACACIA_BARK = new Item(new FabricItemSettings());
	public static final Item DARK_OAK_BARK = new Item(new FabricItemSettings());
	public static final Item CRIMSON_BARK = new Item(new FabricItemSettings());
	public static final Item WARPED_BARK = new Item(new FabricItemSettings());
	public static final Item IRON_ORE_PILE = new Item(new FabricItemSettings());
	public static final Item IRON_ORE_CHUNK = new Item(new FabricItemSettings());
	public static final Item COAL_DUST_PILE = new Item(new FabricItemSettings());
	public static final Item GRAVEL_PILE = new Item(new FabricItemSettings());
	public static final Item CREEPER_OYSTER = new Item(new FabricItemSettings());
	public static final Item ASH_PILE = new Item(new FabricItemSettings());

	public static final Block ASH = new AshBlock(FabricBlockSettings.of(Material.AGGREGATE).strength(0.4F).sounds(BlockSoundGroup.GRAVEL));

	public static final Block LOOSE_COBBLESTONE = new FallingBlock(FabricBlockSettings.of(Material.STONE).strength(1.0F));

	public static final Block OAK_LOG_INNER = new InnerLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_OAK_LOG));
	public static final Block SPRUCE_LOG_INNER = new InnerLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_SPRUCE_LOG));
	public static final Block BIRCH_LOG_INNER = new InnerLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_BIRCH_LOG));
	public static final Block JUNGLE_LOG_INNER = new InnerLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_JUNGLE_LOG));
	public static final Block ACACIA_LOG_INNER = new InnerLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_ACACIA_LOG));
	public static final Block CRIMSON_STEM_INNER = new InnerLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_CRIMSON_STEM));
	public static final Block WARPED_STEM_INNER = new InnerLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_WARPED_STEM));
	public static final Block DARK_OAK_LOG_INNER = new InnerLogBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_DARK_OAK_LOG));

	public static final Block STONE = new StratifiedStoneBlock(FabricBlockSettings.of(Material.STONE).strength(1.0F, 6.0F).requiresTool(), 9, 0, LOOSE_COBBLESTONE, LOOSE_STONE);
	public static final Block HARD_STONE = new StratifiedStoneBlock(FabricBlockSettings.of(Material.STONE).strength(1.75F).requiresTool(), 9, 1, LOOSE_COBBLESTONE, LOOSE_STONE);
	public static final Block DEEP_STONE = new StratifiedStoneBlock(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool(), 9, 2, LOOSE_COBBLESTONE, LOOSE_STONE);

	public static final Block IRON_ORE = new StratifiedOreBlock(FabricBlockSettings.of(Material.STONE).strength(1.75F).requiresTool(), (StratifiedStoneBlock) STONE, IRON_ORE_CHUNK, IRON_ORE_PILE);
	public static final Block HARD_IRON_ORE = new StratifiedOreBlock(FabricBlockSettings.of(Material.STONE).strength(3.0F).requiresTool(), (StratifiedStoneBlock) HARD_STONE, IRON_ORE_CHUNK, IRON_ORE_PILE);
	public static final Block DEEP_IRON_ORE = new StratifiedOreBlock(FabricBlockSettings.of(Material.STONE).strength(5.25F).requiresTool(), (StratifiedStoneBlock) DEEP_STONE, IRON_ORE_CHUNK, IRON_ORE_PILE);
	public static final Block COAL_ORE = new StratifiedOreBlock(FabricBlockSettings.of(Material.STONE).strength(1.5F).requiresTool(), (StratifiedStoneBlock) STONE, Items.COAL, COAL_DUST_PILE);
	public static final Block HARD_COAL_ORE = new StratifiedOreBlock(FabricBlockSettings.of(Material.STONE).strength(2.5F).requiresTool(), (StratifiedStoneBlock) HARD_STONE, Items.COAL, COAL_DUST_PILE);
	public static final Block DEEP_COAL_ORE = new StratifiedOreBlock(FabricBlockSettings.of(Material.STONE).strength(4.5F).requiresTool(), (StratifiedStoneBlock) DEEP_STONE, Items.COAL, COAL_DUST_PILE);

	public static final Block STATIONARY_GRAVEL_SLAB = new SlabBlock(FabricBlockSettings.copyOf(Blocks.GRAVEL));
	public static final Block STATIONARY_LOOSE_COBBLESTONE_SLAB = new SlabBlock(FabricBlockSettings.copyOf(Mbtw.LOOSE_COBBLESTONE));
	public static final Block GRAVEL_SLAB = new FallingSlabBlock(FabricBlockSettings.copyOf(Blocks.GRAVEL), STATIONARY_GRAVEL_SLAB.getDefaultState());
	public static final Block LOOSE_COBBLESTONE_SLAB = new FallingSlabBlock(FabricBlockSettings.copyOf(Mbtw.LOOSE_COBBLESTONE), STATIONARY_LOOSE_COBBLESTONE_SLAB.getDefaultState());

	public static final Item POINTY_STICK = new ChiselItem(10, 1, -2.8F, ToolMaterials.WOOD, new FabricItemSettings());
	public static final Item SHARP_STONE = new ChiselItem(6, 1, -2.8F, ToolMaterials.STONE, new FabricItemSettings());
	public static final Item IRON_CHISEL = new ChiselItem(50, 1, -2.8F, ToolMaterials.IRON, new FabricItemSettings());

	public static final Item FIRE_STRIKER = new FireStarterItem(new FabricItemSettings(), 800, Items.FLINT.getDefaultStack(), 10);
	public static final Item BOW_DRILL = new FireStarterItem(new FabricItemSettings(), 900, ItemStack.EMPTY, 150);
	public static final Item FIRE_PLOUGH = new FireStarterItem(new FabricItemSettings(), 700, ItemStack.EMPTY, 600);

	public static final Block OAK_TRUNK_INNER = new InnerTrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(5.0F).requiresTool());
	public static final Block SPRUCE_TRUNK_INNER = new InnerTrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(5.0F).requiresTool());
	public static final Block BIRCH_TRUNK_INNER = new InnerTrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(5.0F).requiresTool());
	public static final Block JUNGLE_TRUNK_INNER = new InnerTrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(5.0F).requiresTool());
	public static final Block ACACIA_TRUNK_INNER = new InnerTrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(5.0F).requiresTool());
	public static final Block DARK_OAK_TRUNK_INNER = new InnerTrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(5.0F).requiresTool());
	public static final Block CRIMSON_TRUNK_INNER = new InnerTrunkBlock(FabricBlockSettings.of(Material.NETHER_WOOD).sounds(BlockSoundGroup.NETHER_STEM).strength(5.5F).requiresTool());
	public static final Block WARPED_TRUNK_INNER = new InnerTrunkBlock(FabricBlockSettings.of(Material.NETHER_WOOD).sounds(BlockSoundGroup.NETHER_STEM).strength(5.5F).requiresTool());

	public static final Block OAK_TRUNK = new TrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(4.0F).requiresTool(), OAK_TRUNK_INNER);
	public static final Block SPRUCE_TRUNK = new TrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(4.0F).requiresTool(), SPRUCE_TRUNK_INNER);
	public static final Block BIRCH_TRUNK = new TrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(4.0F).requiresTool(), BIRCH_TRUNK_INNER);
	public static final Block JUNGLE_TRUNK = new TrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(4.0F).requiresTool(), JUNGLE_TRUNK_INNER);
	public static final Block ACACIA_TRUNK = new TrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(4.0F).requiresTool(), ACACIA_TRUNK_INNER);
	public static final Block DARK_OAK_TRUNK = new TrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(4.0F).requiresTool(), DARK_OAK_TRUNK_INNER);
	public static final Block CRIMSON_TRUNK = new TrunkBlock(FabricBlockSettings.of(Material.NETHER_WOOD).sounds(BlockSoundGroup.NETHER_STEM).strength(4.5F).requiresTool(), CRIMSON_TRUNK_INNER);
	public static final Block WARPED_TRUNK = new TrunkBlock(FabricBlockSettings.of(Material.NETHER_WOOD).sounds(BlockSoundGroup.NETHER_STEM).strength(4.5F).requiresTool(), WARPED_TRUNK_INNER);

	public static final Block DAMAGED_COBWEB = new DamagedCobwebBlock(FabricBlockSettings.of(Material.COBWEB).noCollision().ticksRandomly().requiresTool().strength(4.0F));

	public static final Block FINITE_TORCH = new FiniteTorchBlock(FabricBlockSettings.copyOf(Blocks.TORCH).luminance(FiniteTorchBlock.createLightLevelFromTorchFire()), ParticleTypes.FLAME);
	public static final Block FINITE_WALL_TORCH = new FiniteWallTorchBlock(FabricBlockSettings.copyOf(Blocks.TORCH).luminance(FiniteTorchBlock.createLightLevelFromTorchFire()), ParticleTypes.FLAME);
	public static BlockEntityType<FiniteTorchBlockEntity> FINITE_TORCH_BLOCK_ENTITY;

	public static final Item FINITE_TORCH_ITEM = new FiniteTorchItem(FINITE_TORCH, FINITE_WALL_TORCH, new FabricItemSettings().maxCount(16), 3100);

	public static final Block CLAY_BRICK = new ClayBrickBlock(FabricBlockSettings.of(Material.DECORATION).breakInstantly().sounds(BlockSoundGroup.SLIME));
	public static BlockEntityType<ClayBrickBlockEntity> CLAY_BRICK_ENTITY;

	public static final Block VARIABLE_CAMPFIRE = new VariableCampfireBlock(true, 1, FabricBlockSettings.copyOf(Blocks.CAMPFIRE).luminance(VariableCampfireBlock.createLightLevelFromFireSize()));
	public static BlockEntityType<VariableCampfireBlockEntity> VARIABLE_CAMPFIRE_ENTITY;

	public static final Block BRICK_OVEN = new BrickOvenBlock(FabricBlockSettings.of(Material.STONE, MapColor.RED).requiresTool().strength(2.0F, 6.0F).luminance((LitStateInvoker.invokeCreateLightLevelFromBlockState(13))));
	public static BlockEntityType<BrickOvenBlockEntity> BRICK_OVEN_ENTITY;
	//    public static final RecipeType<BrickOvenRecipe> BRICK_SMELTING = new RecipeType<BrickOvenRecipe>() {
//        @Override
//        public String toString() {return "brick_smelting";}
//    };
	public static RecipeType<BrickOvenRecipe> BRICK_SMELTING;

	public static final Block MILLSTONE = new MillstoneBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(2.0F, 8.0F));
	public static BlockEntityType<MillstoneBlockEntity> MILLSTONE_ENTITY;
	public static final Block INFINITE_CRANK = new InfiniteCrankBlock(FabricBlockSettings.of(Material.WOOD));
	public static BlockEntityType<InfiniteCrankBlockEntity> INFINITE_CRANK_ENTITY;

	public static final Block AXLE = new AxleBlock(FabricBlockSettings.of(Material.WOOD));
	public static final Block GEARBOX = new GearboxBlock(FabricBlockSettings.of(Material.WOOD));

	public static ItemGroup MBTW_GROUP;

	//public static final RecipeSerializer<BrickOvenRecipe> BRICK_SMELTING_SERIALIZER = new CookingRecipeSerializer<>(BrickOvenRecipe::new, 100);
	public static RecipeSerializer<BrickOvenRecipe> BRICK_SMELTING_SERIALIZER;
	//public static final ScreenHandlerType<BrickOvenScreenHandler> BRICK_OVEN_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MOD_ID, "brick_oven"), BrickOvenScreenHandler::new);
	public static final ScreenHandlerType<BrickOvenScreenHandler> BRICK_OVEN_SCREEN_HANDLER = new ScreenHandlerType<>(BrickOvenScreenHandler::new);

	public static final ScreenHandlerType<CraftingScreenHandler> TRUNK_WORKBENCH_SCREEN_HANDLER = new ScreenHandlerType<>(TrunkWorkbenchScreenHandler::new);

	//    public static final RuleTest RULE_HARD_STONE = new BlockMatchRuleTest(HARD_STONE);
//    public static final RuleTest RULE_DEEP_STONE = new BlockMatchRuleTest(DEEP_STONE);
//    public static final ConfiguredFeature<?, ?> ORE_COAL = Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, COAL_ORE.getDefaultState(), 17)).rangeOf(128).spreadHorizontally().repeat(13);
//    public static final ConfiguredFeature<?, ?> ORE_COAL_HARD = Feature.ORE.configure(new OreFeatureConfig(RULE_HARD_STONE, HARD_COAL_ORE.getDefaultState(), 17)).rangeOf(HARD_STONE_MAX).spreadHorizontally().repeat(5);
//    public static final ConfiguredFeature<?, ?> ORE_COAL_DEEP = Feature.ORE.configure(new OreFeatureConfig(RULE_DEEP_STONE, DEEP_COAL_ORE.getDefaultState(), 17)).rangeOf(DEEP_STONE_MAX).spreadHorizontally().repeat(4);
//    public static final ConfiguredFeature<?, ?> ORE_IRON = Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, IRON_ORE.getDefaultState(), 9)).rangeOf(64).spreadHorizontally().repeat(14);
//    public static final ConfiguredFeature<?, ?> ORE_IRON_HARD = Feature.ORE.configure(new OreFeatureConfig(RULE_HARD_STONE, HARD_IRON_ORE.getDefaultState(), 9)).rangeOf(HARD_STONE_MAX).spreadHorizontally().repeat(6);
//    public static final ConfiguredFeature<?, ?> ORE_IRON_DEEP = Feature.ORE.configure(new OreFeatureConfig(RULE_DEEP_STONE, DEEP_IRON_ORE.getDefaultState(), 9)).rangeOf(DEEP_STONE_MAX).spreadHorizontally().repeat(5);

	static {
		Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(MOD_ID, "brick_smelting"), new CookingRecipeSerializer<>(BrickOvenRecipe::new, 100));
		BRICK_SMELTING = Registry.register(Registries.RECIPE_TYPE, new Identifier(MOD_ID, "brick_smelting"), new RecipeType<BrickOvenRecipe>() {
			@Override
			public String toString() {
				return "brick_smelting";
			}
		});
	}

	@Override
	public void onInitialize() {

		MBTW_GROUP = FabricItemGroup.builder(new Identifier(MOD_ID, "mbtw_group"))
				.displayName(Text.literal("MBTW"))
				.icon(() -> new ItemStack(LOOSE_STONE))
				.entries((enabledFeatures, entries, operatorEnabled) -> {
					entries.add(LOOSE_STONE);
					entries.add(IRON_ORE_PILE);
				})
				.build();

		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "empty"), MBTW_EMPTY);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "loose_stone"), LOOSE_STONE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "iron_ore_pile"), IRON_ORE_PILE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "iron_ore_chunk"), IRON_ORE_CHUNK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "coal_dust_pile"), COAL_DUST_PILE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "saw_dust"), SAW_DUST);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "fungal_dust"), FUNGAL_DUST);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "oak_bark"), OAK_BARK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "spruce_bark"), SPRUCE_BARK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "birch_bark"), BIRCH_BARK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "jungle_bark"), JUNGLE_BARK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "acacia_bark"), ACACIA_BARK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "dark_oak_bark"), DARK_OAK_BARK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "crimson_bark"), CRIMSON_BARK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "warped_bark"), WARPED_BARK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "gravel_pile"), GRAVEL_PILE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "ash_pile"), ASH_PILE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "creeper_oyster"), CREEPER_OYSTER);

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "ash"), ASH);

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "loose_cobblestone"), LOOSE_COBBLESTONE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "loose_cobblestone"), new BlockItem(LOOSE_COBBLESTONE, new FabricItemSettings()));

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "oak_log_inner"), OAK_LOG_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "spruce_log_inner"), SPRUCE_LOG_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "birch_log_inner"), BIRCH_LOG_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "jungle_log_inner"), JUNGLE_LOG_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "acacia_log_inner"), ACACIA_LOG_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "crimson_stem_inner"), CRIMSON_STEM_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "warped_stem_inner"), WARPED_STEM_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "dark_oak_log_inner"), DARK_OAK_LOG_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "oak_trunk"), OAK_TRUNK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "oak_trunk"), new BlockItem(OAK_TRUNK, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "spruce_trunk"), SPRUCE_TRUNK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "spruce_trunk"), new BlockItem(SPRUCE_TRUNK, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "birch_trunk"), BIRCH_TRUNK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "birch_trunk"), new BlockItem(BIRCH_TRUNK, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "jungle_trunk"), JUNGLE_TRUNK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "jungle_trunk"), new BlockItem(JUNGLE_TRUNK, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "acacia_trunk"), ACACIA_TRUNK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "acacia_trunk"), new BlockItem(ACACIA_TRUNK, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "dark_oak_trunk"), DARK_OAK_TRUNK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "dark_oak_trunk"), new BlockItem(DARK_OAK_TRUNK, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "crimson_trunk"), CRIMSON_TRUNK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "crimson_trunk"), new BlockItem(CRIMSON_TRUNK, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "warped_trunk"), WARPED_TRUNK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "warped_trunk"), new BlockItem(WARPED_TRUNK, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "oak_trunk_inner"), OAK_TRUNK_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "spruce_trunk_inner"), SPRUCE_TRUNK_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "birch_trunk_inner"), BIRCH_TRUNK_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "jungle_trunk_inner"), JUNGLE_TRUNK_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "acacia_trunk_inner"), ACACIA_TRUNK_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "dark_oak_trunk_inner"), DARK_OAK_TRUNK_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "crimson_trunk_inner"), CRIMSON_TRUNK_INNER);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "warped_trunk_inner"), WARPED_TRUNK_INNER);

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "stone"), STONE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "stone"), new BlockItem(STONE, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "hard_stone"), HARD_STONE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "hard_stone"), new BlockItem(HARD_STONE, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "deep_stone"), DEEP_STONE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "deep_stone"), new BlockItem(DEEP_STONE, new FabricItemSettings()));

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "iron_ore"), IRON_ORE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "iron_ore"), new BlockItem(IRON_ORE, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "hard_iron_ore"), HARD_IRON_ORE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "hard_iron_ore"), new BlockItem(HARD_IRON_ORE, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "deep_iron_ore"), DEEP_IRON_ORE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "deep_iron_ore"), new BlockItem(DEEP_IRON_ORE, new FabricItemSettings()));

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "coal_ore"), COAL_ORE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "coal_ore"), new BlockItem(COAL_ORE, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "hard_coal_ore"), HARD_COAL_ORE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "hard_coal_ore"), new BlockItem(HARD_COAL_ORE, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "deep_coal_ore"), DEEP_COAL_ORE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "deep_coal_ore"), new BlockItem(DEEP_COAL_ORE, new FabricItemSettings()));

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "stationary_gravel_slab"), STATIONARY_GRAVEL_SLAB);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "stationary_loose_cobblestone_slab"), STATIONARY_LOOSE_COBBLESTONE_SLAB);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "gravel_slab"), GRAVEL_SLAB);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "gravel_slab"), new BlockItem(GRAVEL_SLAB, new FabricItemSettings()));
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "loose_cobblestone_slab"), LOOSE_COBBLESTONE_SLAB);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "loose_cobblestone_slab"), new BlockItem(LOOSE_COBBLESTONE_SLAB, new FabricItemSettings()));

		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "pointy_stick"), POINTY_STICK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "sharp_stone"), SHARP_STONE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "iron_chisel"), IRON_CHISEL);

		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "fire_striker"), FIRE_STRIKER);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "bow_drill"), BOW_DRILL);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "fire_plough"), FIRE_PLOUGH);

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "damaged_cobweb"), DAMAGED_COBWEB);

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "finite_torch"), FINITE_TORCH);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "finite_torch"), (BlockItem)FINITE_TORCH_ITEM);
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "finite_wall_torch"), FINITE_WALL_TORCH);
		FINITE_TORCH_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "finite_torch"), FabricBlockEntityTypeBuilder.create((pos, state) -> new FiniteTorchBlockEntity(FINITE_TORCH_BLOCK_ENTITY, pos, state), FINITE_TORCH, FINITE_WALL_TORCH).build(null));

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "clay_brick"), CLAY_BRICK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "clay_brick"), new BlockItem(CLAY_BRICK, new FabricItemSettings()));
		CLAY_BRICK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "clay_brick"), FabricBlockEntityTypeBuilder.create(ClayBrickBlockEntity::new, CLAY_BRICK).build(null));

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "variable_campfire"), VARIABLE_CAMPFIRE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "variable_campfire"), new BlockItem(VARIABLE_CAMPFIRE, new FabricItemSettings()));
		VARIABLE_CAMPFIRE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "variable_campfire"), FabricBlockEntityTypeBuilder.create(VariableCampfireBlockEntity::new, VARIABLE_CAMPFIRE).build(null));

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "brick_oven"), BRICK_OVEN);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "brick_oven"), new BlockItem(BRICK_OVEN, new FabricItemSettings()));
		BRICK_OVEN_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "brick_oven"), FabricBlockEntityTypeBuilder.create(BrickOvenBlockEntity::new, BRICK_OVEN).build(null));

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "millstone"), MILLSTONE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "millstone"), new BlockItem(MILLSTONE, new FabricItemSettings()));
		MILLSTONE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "millstone"), FabricBlockEntityTypeBuilder.create(MillstoneBlockEntity::new, MILLSTONE).build(null));

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "infinite_crank"), INFINITE_CRANK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "infinite_crank"), new BlockItem(INFINITE_CRANK, new FabricItemSettings()));
		INFINITE_CRANK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "infinite_crank"), FabricBlockEntityTypeBuilder.create(InfiniteCrankBlockEntity::new, INFINITE_CRANK).build(null));

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "axle"), AXLE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "axle"), new BlockItem(AXLE, new FabricItemSettings()));

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "gearbox"), GEARBOX);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "gearbox"), new BlockItem(GEARBOX, new FabricItemSettings()));

		MbtwApi.SOURCE_API.registerForBlocks(MbtwApi::findSource, GEARBOX);
		MbtwApi.SINK_API.registerForBlocks(MbtwApi::findSink, MILLSTONE);

		//BRICK_SMELTING = RecipeType.register("mbtw:brick_smelting");
		//RecipeSerializer.register("mbtw:brick_smelting", BRICK_SMELTING_SERIALIZER);
		Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "brick_oven"), BRICK_OVEN_SCREEN_HANDLER);

//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(MOD_ID, "ore_coal"), ORE_COAL);
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(MOD_ID, "ore_coal_hard"), ORE_COAL_HARD);
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(MOD_ID, "ore_coal_deep"), ORE_COAL_DEEP);
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(MOD_ID, "ore_iron"), ORE_IRON);
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(MOD_ID, "ore_iron_hard"), ORE_IRON_HARD);
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(MOD_ID, "ore_iron_deep"), ORE_IRON_DEEP);

		FuelRegistry.INSTANCE.add(SAW_DUST, 100);
		FuelRegistry.INSTANCE.add(POINTY_STICK, 100);
		FuelRegistry.INSTANCE.add(FIRE_PLOUGH, 200);
		FuelRegistry.INSTANCE.add(BOW_DRILL, 200);
		FuelRegistry.INSTANCE.add(MbtwTagsMaps.BARK, 100);

		MbtwLootModifier.modifyLootTables();
	}
}
