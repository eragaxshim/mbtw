package mbtw.mbtw;

import mbtw.mbtw.block.*;
import mbtw.mbtw.block.entity.BrickBlockEntity;
import mbtw.mbtw.block.entity.BrickOvenBlockEntity;
import mbtw.mbtw.item.ChiselItem;
import mbtw.mbtw.mixin.LitStateInvoker;
import mbtw.mbtw.recipe.BrickOvenRecipe;
import mbtw.mbtw.screen.BrickOvenScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class Mbtw implements ModInitializer {
    public static int DEEP_STONE_MAX = 25;
    public static int HARD_STONE_MAX = 45;

    public static final Material MBTW_HARD_STRATIFIED = (new Material.Builder(MaterialColor.STONE)).build();
    public static final Material MBTW_DEEP_STRATIFIED = (new Material.Builder(MaterialColor.STONE)).build();

    public static final Item MBTW_LOOSE_STONE = new Item((new FabricItemSettings().group(ItemGroup.MATERIALS)));
    public static final Item MBTW_IRON_ORE_PILE = new Item((new FabricItemSettings().group(ItemGroup.MATERIALS)));
    public static final Item MBTW_IRON_ORE_CHUNK = new Item((new FabricItemSettings().group(ItemGroup.MATERIALS)));
    public static final Item MBTW_COAL_DUST_PILE = new Item((new FabricItemSettings().group(ItemGroup.MATERIALS)));

    public static final Block MBTW_LOOSE_COBBLESTONE = new FallingBlock(FabricBlockSettings.of(Material.STONE).strength(0.7f));

    public static final Block OAK_LOG_INNER = new InnerLogBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(0.2f));
    public static final Block SPRUCE_LOG_INNER = new InnerLogBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(0.2f));

    public static final Block OAK_TRUNK_INNER = new InnerTrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(0.2f));
    public static final Block SPRUCE_TRUNK_INNER = new InnerTrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(0.2f));
    public static final Block OAK_TRUNK = new TrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(0.2f), OAK_TRUNK_INNER);
    public static final Block SPRUCE_TRUNK = new TrunkBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(0.2f), SPRUCE_TRUNK_INNER);

    public static final Block MBTW_STONE = new StratifiedStoneBlock(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F), 9, 0, MBTW_LOOSE_COBBLESTONE, MBTW_LOOSE_STONE);
    public static final Block MBTW_HARD_STONE = new StratifiedStoneBlock(FabricBlockSettings.of(MBTW_HARD_STRATIFIED).strength(2.0f), 9, 1, MBTW_LOOSE_COBBLESTONE, MBTW_LOOSE_STONE);
    public static final Block MBTW_DEEP_STONE = new StratifiedStoneBlock(FabricBlockSettings.of(MBTW_DEEP_STRATIFIED).strength(2.5f), 9, 2, MBTW_LOOSE_COBBLESTONE, MBTW_LOOSE_STONE);

    public static final Block MBTW_IRON_ORE = new StratifiedOreBlock(FabricBlockSettings.of(Material.STONE).strength(2.0f), (StratifiedStoneBlock) MBTW_STONE, MBTW_IRON_ORE_CHUNK, MBTW_IRON_ORE_PILE);
    public static final Block MBTW_HARD_IRON_ORE = new StratifiedOreBlock(FabricBlockSettings.of(MBTW_HARD_STRATIFIED).strength(2.5f), (StratifiedStoneBlock) MBTW_HARD_STONE, MBTW_IRON_ORE_CHUNK, MBTW_IRON_ORE_PILE);
    public static final Block MBTW_DEEP_IRON_ORE = new StratifiedOreBlock(FabricBlockSettings.of(MBTW_DEEP_STRATIFIED).strength(3.0f), (StratifiedStoneBlock) MBTW_DEEP_STONE, MBTW_IRON_ORE_CHUNK, MBTW_IRON_ORE_PILE);
    public static final Block MBTW_COAL_ORE = new StratifiedOreBlock(FabricBlockSettings.of(Material.STONE).strength(2.0f), (StratifiedStoneBlock) MBTW_STONE, Items.COAL, MBTW_COAL_DUST_PILE);
    public static final Block MBTW_HARD_COAL_ORE = new StratifiedOreBlock(FabricBlockSettings.of(MBTW_HARD_STRATIFIED).strength(2.5f), (StratifiedStoneBlock) MBTW_HARD_STONE, Items.COAL, MBTW_COAL_DUST_PILE);
    public static final Block MBTW_DEEP_COAL_ORE = new StratifiedOreBlock(FabricBlockSettings.of(MBTW_DEEP_STRATIFIED).strength(3.0f), (StratifiedStoneBlock) MBTW_DEEP_STONE, Items.COAL, MBTW_COAL_DUST_PILE);

    public static final Item MBTW_GRAVEL_PILE = new Item((new FabricItemSettings().group(ItemGroup.MATERIALS)));

    public static final Block MBTW_GRAVEL_SLAB = new FallingSlabBlock(-8356741, FabricBlockSettings.of(Material.SOIL, MaterialColor.DIRT).strength(0.05f).sounds(BlockSoundGroup.GRAVEL));
    public static final Block MBTW_LOOSE_COBBLESTONE_SLAB = new FallingSlabBlock(MaterialColor.STONE.color, FabricBlockSettings.of(Material.STONE).strength(0.7f).sounds(BlockSoundGroup.STONE));

    public static final Item POINTY_STICK = new ChiselItem(10, 1, -2.8F, ToolMaterials.WOOD, new FabricItemSettings().group(ItemGroup.TOOLS));
    public static final Item SHARP_STONE = new ChiselItem(6, 1, -2.8F, ToolMaterials.STONE, new FabricItemSettings().group(ItemGroup.TOOLS));
    public static final Item IRON_CHISEL = new ChiselItem(50, 1, -2.8F, ToolMaterials.IRON, new FabricItemSettings().group(ItemGroup.TOOLS));

    public static final Block CLAY_BRICK = new BrickBlock(FabricBlockSettings.of(Material.ORGANIC_PRODUCT).strength(0.1f).sounds(BlockSoundGroup.GRAVEL));
    public static BlockEntityType<BrickBlockEntity> CLAY_BRICK_ENTITY;

    public static final Block BRICK_OVEN = new BrickOvenBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).requiresTool().strength(2.0F, 6.0F).luminance((LitStateInvoker.invokeCreateLightLevelFromBlockState(13))));
    public static BlockEntityType<BrickOvenBlockEntity> BRICK_OVEN_ENTITY;
    public static final RecipeType<BrickOvenRecipe> BRICK_SMELTING = new RecipeType<BrickOvenRecipe>() {
        @Override
        public String toString() {return "brick_smelting";}
    };
    public static final RecipeSerializer<BrickOvenRecipe> BRICK_SMELTING_SERIALIZER = new CookingRecipeSerializer(BrickOvenRecipe::new, 100);
    public static final ScreenHandlerType<BrickOvenScreenHandler> BRICK_OVEN_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("mbtw", "brick_oven"), BrickOvenScreenHandler::new);

    public static final RuleTest RULE_HARD_STONE = new BlockMatchRuleTest(MBTW_HARD_STONE);
    public static final RuleTest RULE_DEEP_STONE = new BlockMatchRuleTest(MBTW_DEEP_STONE);
    public static final ConfiguredFeature<?, ?> ORE_COAL = (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, MBTW_COAL_ORE.getDefaultState(), 17)).rangeOf(128)).spreadHorizontally()).repeat(13);
    public static final ConfiguredFeature<?, ?> ORE_COAL_HARD = (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configure(new OreFeatureConfig(RULE_HARD_STONE, MBTW_HARD_COAL_ORE.getDefaultState(), 17)).rangeOf(HARD_STONE_MAX)).spreadHorizontally()).repeat(5);
    public static final ConfiguredFeature<?, ?> ORE_COAL_DEEP = (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configure(new OreFeatureConfig(RULE_DEEP_STONE, MBTW_DEEP_COAL_ORE.getDefaultState(), 17)).rangeOf(DEEP_STONE_MAX)).spreadHorizontally()).repeat(4);
    public static final ConfiguredFeature<?, ?> ORE_IRON = (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, MBTW_IRON_ORE.getDefaultState(), 9)).rangeOf(64)).spreadHorizontally()).repeat(14);
    public static final ConfiguredFeature<?, ?> ORE_IRON_HARD = (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configure(new OreFeatureConfig(RULE_HARD_STONE, MBTW_HARD_IRON_ORE.getDefaultState(), 9)).rangeOf(HARD_STONE_MAX)).spreadHorizontally()).repeat(6);
    public static final ConfiguredFeature<?, ?> ORE_IRON_DEEP = (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configure(new OreFeatureConfig(RULE_DEEP_STONE, MBTW_DEEP_IRON_ORE.getDefaultState(), 9)).rangeOf(DEEP_STONE_MAX)).spreadHorizontally()).repeat(5);

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("mbtw", "loose_stone"), MBTW_LOOSE_STONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "iron_ore_pile"), MBTW_IRON_ORE_PILE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "iron_ore_chunk"), MBTW_IRON_ORE_CHUNK);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "coal_dust_pile"), MBTW_COAL_DUST_PILE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "gravel_pile"), MBTW_GRAVEL_PILE);

        Registry.register(Registry.BLOCK, new Identifier("mbtw", "loose_cobblestone"), MBTW_LOOSE_COBBLESTONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "loose_cobblestone"), new BlockItem(MBTW_LOOSE_COBBLESTONE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier("mbtw", "oak_log_inner"), OAK_LOG_INNER);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "oak_log_inner"), new BlockItem(OAK_LOG_INNER, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "spruce_log_inner"), SPRUCE_LOG_INNER);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "spruce_log_inner"), new BlockItem(SPRUCE_LOG_INNER, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "oak_trunk"), OAK_TRUNK);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "oak_trunk"), new BlockItem(OAK_TRUNK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "spruce_trunk"), SPRUCE_TRUNK);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "spruce_trunk"), new BlockItem(SPRUCE_TRUNK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "oak_trunk_inner"), OAK_TRUNK_INNER);
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "spruce_trunk_inner"), SPRUCE_TRUNK_INNER);

        Registry.register(Registry.BLOCK, new Identifier("mbtw", "stone"), MBTW_STONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "stone"), new BlockItem(MBTW_STONE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "hard_stone"), MBTW_HARD_STONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "hard_stone"), new BlockItem(MBTW_HARD_STONE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "deep_stone"), MBTW_DEEP_STONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "deep_stone"), new BlockItem(MBTW_DEEP_STONE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier("mbtw", "iron_ore"), MBTW_IRON_ORE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "iron_ore"), new BlockItem(MBTW_IRON_ORE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "hard_iron_ore"), MBTW_HARD_IRON_ORE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "hard_iron_ore"), new BlockItem(MBTW_HARD_IRON_ORE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "deep_iron_ore"), MBTW_DEEP_IRON_ORE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "deep_iron_ore"), new BlockItem(MBTW_DEEP_IRON_ORE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier("mbtw", "coal_ore"), MBTW_COAL_ORE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "coal_ore"), new BlockItem(MBTW_COAL_ORE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "hard_coal_ore"), MBTW_HARD_COAL_ORE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "hard_coal_ore"), new BlockItem(MBTW_HARD_COAL_ORE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "deep_coal_ore"), MBTW_DEEP_COAL_ORE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "deep_coal_ore"), new BlockItem(MBTW_DEEP_COAL_ORE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier("mbtw", "gravel_slab"), MBTW_GRAVEL_SLAB);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "gravel_slab"), new BlockItem(MBTW_GRAVEL_SLAB, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "loose_cobblestone_slab"), MBTW_LOOSE_COBBLESTONE_SLAB);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "loose_cobblestone_slab"), new BlockItem(MBTW_LOOSE_COBBLESTONE_SLAB, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.ITEM, new Identifier("mbtw", "pointy_stick"), POINTY_STICK);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "sharp_stone"), SHARP_STONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "iron_chisel"), IRON_CHISEL);

        Registry.register(Registry.BLOCK, new Identifier("mbtw", "clay_brick"), CLAY_BRICK);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "clay_brick"), new BlockItem(CLAY_BRICK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        CLAY_BRICK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("mbtw", "clay_brick"), BlockEntityType.Builder.create(BrickBlockEntity::new, CLAY_BRICK).build(null));

        Registry.register(Registry.BLOCK, new Identifier("mbtw", "brick_oven"), BRICK_OVEN);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "brick_oven"), new BlockItem(BRICK_OVEN, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        BRICK_OVEN_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("mbtw", "brick_oven"), BlockEntityType.Builder.create(BrickOvenBlockEntity::new, BRICK_OVEN).build(null));

        Registry.register(Registry.RECIPE_TYPE, new Identifier("mbtw", "brick_smelting"), BRICK_SMELTING);
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier("mbtw", "brick_smelting"), BRICK_SMELTING_SERIALIZER);

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("mbtw", "ore_coal"), ORE_COAL);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("mbtw", "ore_coal_hard"), ORE_COAL_HARD);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("mbtw", "ore_coal_deep"), ORE_COAL_DEEP);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("mbtw", "ore_iron"), ORE_IRON);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("mbtw", "ore_iron_hard"), ORE_IRON_HARD);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("mbtw", "ore_iron_deep"), ORE_IRON_DEEP);

    }
}
