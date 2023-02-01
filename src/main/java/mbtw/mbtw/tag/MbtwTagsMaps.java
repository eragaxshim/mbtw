package mbtw.mbtw.tag;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.InnerLogBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class MbtwTagsMaps {
    public static final TagKey<Block> DEEP_STONE_OVERWORLD = registerBlockTagKey("deep_stone_overworld");
    public static final TagKey<Block> BREAK_INTERCEPTABLES = registerBlockTagKey("break_interceptables");
    public static final TagKey<Block> TRUNKS = registerBlockTagKey("trunks");
    public static final TagKey<Block> STRIPPABLES = registerBlockTagKey("strippables");
    public static final TagKey<Block> EASY_HAND_BREAKABLES = registerBlockTagKey("easy_hand_breakables");
    public static final TagKey<Block> HAND_UNBREAKABLES = registerBlockTagKey("hand_unbreakables");
    public static final TagKey<Block> TOOL_REDUCED_EFFECTIVENESS = registerBlockTagKey("tool_reduced_effectiveness");
    public static final TagKey<Block> CHISEL_MINEABLE = registerBlockTagKey("mineable/chisel");

    public static final TagKey<Item> CHISELS = registerItemTagKey("chisels");
    public static final TagKey<Item> BARK = registerItemTagKey("bark");

    public static final ImmutableMap<Block, BlockState> INNER_LOG_MAP = ImmutableMap.<Block, BlockState>builder()
            .put(Blocks.OAK_LOG, Mbtw.OAK_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.OAK_WOOD, Mbtw.OAK_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.STRIPPED_OAK_LOG, Mbtw.OAK_LOG_INNER.getDefaultState())
            .put(Blocks.STRIPPED_OAK_WOOD, Mbtw.OAK_LOG_INNER.getDefaultState())
            .put(Blocks.SPRUCE_LOG, Mbtw.SPRUCE_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.SPRUCE_WOOD, Mbtw.SPRUCE_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.STRIPPED_SPRUCE_LOG, Mbtw.SPRUCE_LOG_INNER.getDefaultState())
            .put(Blocks.STRIPPED_SPRUCE_WOOD, Mbtw.SPRUCE_LOG_INNER.getDefaultState())
            .put(Blocks.BIRCH_LOG, Mbtw.BIRCH_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.BIRCH_WOOD, Mbtw.BIRCH_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.STRIPPED_BIRCH_LOG, Mbtw.BIRCH_LOG_INNER.getDefaultState())
            .put(Blocks.STRIPPED_BIRCH_WOOD, Mbtw.BIRCH_LOG_INNER.getDefaultState())
            .put(Blocks.JUNGLE_LOG, Mbtw.JUNGLE_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.JUNGLE_WOOD, Mbtw.JUNGLE_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.STRIPPED_JUNGLE_LOG, Mbtw.JUNGLE_LOG_INNER.getDefaultState())
            .put(Blocks.STRIPPED_JUNGLE_WOOD, Mbtw.JUNGLE_LOG_INNER.getDefaultState())
            .put(Blocks.ACACIA_LOG, Mbtw.ACACIA_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.ACACIA_WOOD, Mbtw.ACACIA_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.STRIPPED_ACACIA_LOG, Mbtw.ACACIA_LOG_INNER.getDefaultState())
            .put(Blocks.STRIPPED_ACACIA_WOOD, Mbtw.ACACIA_LOG_INNER.getDefaultState())
            .put(Blocks.DARK_OAK_LOG, Mbtw.DARK_OAK_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.DARK_OAK_WOOD, Mbtw.DARK_OAK_LOG_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.STRIPPED_DARK_OAK_LOG, Mbtw.DARK_OAK_LOG_INNER.getDefaultState())
            .put(Blocks.STRIPPED_DARK_OAK_WOOD, Mbtw.DARK_OAK_LOG_INNER.getDefaultState())
            .put(Blocks.CRIMSON_STEM, Mbtw.CRIMSON_STEM_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.CRIMSON_HYPHAE, Mbtw.CRIMSON_STEM_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.STRIPPED_CRIMSON_STEM, Mbtw.CRIMSON_STEM_INNER.getDefaultState())
            .put(Blocks.STRIPPED_CRIMSON_HYPHAE, Mbtw.CRIMSON_STEM_INNER.getDefaultState())
            .put(Blocks.WARPED_STEM, Mbtw.WARPED_STEM_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.WARPED_HYPHAE, Mbtw.WARPED_STEM_INNER.getDefaultState().with(InnerLogBlock.BREAK_LEVEL, 5))
            .put(Blocks.STRIPPED_WARPED_STEM, Mbtw.WARPED_STEM_INNER.getDefaultState())
            .put(Blocks.STRIPPED_WARPED_HYPHAE, Mbtw.WARPED_STEM_INNER.getDefaultState())
            .build();

    public static final ImmutableMap<String, Pair<Identifier, Float>> RECIPE_COPY_MAP = ImmutableMap.<String, Pair<Identifier, Float>>builder()
            .put("baked_potato", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("black_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("blue_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("brown_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("coal_from_smelting", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("cooked_beef", new Pair<>(new Identifier("mbtw", "brick_smelting"), 2.5F))
            .put("cooked_chicken", new Pair<>(new Identifier("mbtw", "brick_smelting"), 2.5F))
            .put("cooked_cod", new Pair<>(new Identifier("mbtw", "brick_smelting"), 2.5F))
            .put("cooked_mutton", new Pair<>(new Identifier("mbtw", "brick_smelting"), 2.5F))
            .put("cooked_porkchop", new Pair<>(new Identifier("mbtw", "brick_smelting"), 2.5F))
            .put("cooked_rabbit", new Pair<>(new Identifier("mbtw", "brick_smelting"), 2.5F))
            .put("cooked_salmon", new Pair<>(new Identifier("mbtw", "brick_smelting"), 2.5F))
            .put("cracked_nether_bricks", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("cracked_polished_blackstone_bricks", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("cracked_stone_bricks", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("cyan_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("diamond_from_smelting", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("dried_kelp_from_smelting", new Pair<>(new Identifier("mbtw", "brick_smelting"), 2.5F))
            .put("emerald_from_smelting", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("glass", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("gold_ingot", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("gray_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("green_dye", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("green_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("lapis_from_smelting", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("light_blue_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("light_gray_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("lime_dye_from_smelting", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("lime_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("magenta_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("netherite_scrap", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("nether_brick", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("orange_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("pink_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("popped_chorus_fruit", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("purple_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("quartz", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("redstone_from_smelting", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("red_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("smooth_quartz", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("smooth_red_sandstone", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("smooth_sandstone", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("smooth_stone", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("sponge", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("stone", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("white_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .put("yellow_glazed_terracotta", new Pair<>(new Identifier("mbtw", "brick_smelting"), 10.0F))
            .build();

    public static final ImmutableMap<Block, BlockState> LOG_TRUNK_MAP = ImmutableMap.<Block, BlockState>builder()
            .put(Blocks.OAK_LOG, Mbtw.OAK_TRUNK.getDefaultState())
            .put(Blocks.SPRUCE_LOG, Mbtw.SPRUCE_TRUNK.getDefaultState())
            .put(Blocks.BIRCH_LOG, Mbtw.BIRCH_TRUNK.getDefaultState())
            .put(Blocks.JUNGLE_LOG, Mbtw.JUNGLE_TRUNK.getDefaultState())
            .put(Blocks.ACACIA_LOG, Mbtw.ACACIA_TRUNK.getDefaultState())
            .put(Blocks.DARK_OAK_LOG, Mbtw.DARK_OAK_TRUNK.getDefaultState())
            .put(Blocks.CRIMSON_STEM, Mbtw.CRIMSON_TRUNK.getDefaultState())
            .put(Blocks.WARPED_STEM, Mbtw.WARPED_TRUNK.getDefaultState())
            .build();

    public static final ImmutableSet<Material> HAND_BREAKABLE_MATERIALS = ImmutableSet.<Material>builder()
            .add(Material.PLANT)
            .add(Material.REPLACEABLE_PLANT)
            .add(Material.GOURD)
            .add(Material.UNDERWATER_PLANT)
            .add(Material.REPLACEABLE_UNDERWATER_PLANT)
            .add(Material.NETHER_SHOOTS)
            .add(Material.SNOW_LAYER)
            .add(Material.SNOW_BLOCK)
            .add(Material.AGGREGATE)
            .add(Material.BAMBOO_SAPLING)
            .add(Material.WOOL)
            .add(Material.LEAVES)
            .add(Material.GLASS)
            .add(Material.CAKE)
            .add(Material.CACTUS)
            .build();

    private void MbtwTagKeysMaps() { }

    private static TagKey<Item> registerItemTagKey(String id) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(Mbtw.MOD_ID, id));
    }

    private static TagKey<Block> registerBlockTagKey(String id) {
        return TagKey.of(RegistryKeys.BLOCK, new Identifier(Mbtw.MOD_ID, id));
    }
}
