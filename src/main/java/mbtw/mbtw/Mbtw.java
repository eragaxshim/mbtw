package mbtw.mbtw;

import mbtw.mbtw.block.FallingSlabBlock;
import mbtw.mbtw.block.MultiBreakBlock;
import mbtw.mbtw.block.StratifiedOreBlock;
import mbtw.mbtw.item.ChiselItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Mbtw implements ModInitializer {
    public static final Material MBTW_HARD_STRATIFIED = (new Material.Builder(MaterialColor.STONE)).build();
    public static final Material MBTW_DEEP_STRATIFIED = (new Material.Builder(MaterialColor.STONE)).build();

    public static final Item MBTW_LOOSE_STONE = new Item((new FabricItemSettings().group(ItemGroup.MATERIALS)));
    public static final Item MBTW_IRON_ORE_PILE = new Item((new FabricItemSettings().group(ItemGroup.MATERIALS)));
    public static final Item MBTW_IRON_ORE_CHUNK = new Item((new FabricItemSettings().group(ItemGroup.MATERIALS)));
    public static final Item MBTW_COAL_DUST_PILE = new Item((new FabricItemSettings().group(ItemGroup.MATERIALS)));

    public static final Block MBTW_LOOSE_COBBLESTONE = new FallingBlock(FabricBlockSettings.of(Material.STONE).strength(1.0f));
    public static final Block MBTW_STONE = new MultiBreakBlock(FabricBlockSettings.of(Material.STONE).strength(1.5f), 9, 0, MBTW_LOOSE_COBBLESTONE, MBTW_LOOSE_STONE);
    public static final Block MBTW_HARD_STONE = new MultiBreakBlock(FabricBlockSettings.of(MBTW_HARD_STRATIFIED).strength(2.0f), 9, 1, MBTW_LOOSE_COBBLESTONE, MBTW_LOOSE_STONE);
    public static final Block MBTW_DEEP_STONE = new MultiBreakBlock(FabricBlockSettings.of(MBTW_DEEP_STRATIFIED).strength(2.5f), 9, 2, MBTW_LOOSE_COBBLESTONE, MBTW_LOOSE_STONE);

    public static final Block MBTW_IRON_ORE = new StratifiedOreBlock(FabricBlockSettings.of(Material.STONE).strength(2.0f), (MultiBreakBlock) MBTW_STONE, MBTW_IRON_ORE_CHUNK, MBTW_IRON_ORE_PILE);
    public static final Block MBTW_HARD_IRON_ORE = new StratifiedOreBlock(FabricBlockSettings.of(MBTW_HARD_STRATIFIED).strength(2.5f), (MultiBreakBlock) MBTW_HARD_STONE, MBTW_IRON_ORE_CHUNK, MBTW_IRON_ORE_PILE);
    public static final Block MBTW_DEEP_IRON_ORE = new StratifiedOreBlock(FabricBlockSettings.of(MBTW_DEEP_STRATIFIED).strength(3.0f), (MultiBreakBlock) MBTW_DEEP_STONE, MBTW_IRON_ORE_CHUNK, MBTW_IRON_ORE_PILE);
    public static final Block MBTW_COAL_ORE = new StratifiedOreBlock(FabricBlockSettings.of(Material.STONE).strength(2.0f), (MultiBreakBlock) MBTW_STONE, MBTW_COAL_DUST_PILE, Items.COAL);
    public static final Block MBTW_HARD_COAL_ORE = new StratifiedOreBlock(FabricBlockSettings.of(MBTW_HARD_STRATIFIED).strength(2.5f), (MultiBreakBlock) MBTW_HARD_STONE, Items.COAL, MBTW_COAL_DUST_PILE);
    public static final Block MBTW_DEEP_COAL_ORE = new StratifiedOreBlock(FabricBlockSettings.of(MBTW_DEEP_STRATIFIED).strength(3.0f), (MultiBreakBlock) MBTW_DEEP_STONE, Items.COAL, MBTW_COAL_DUST_PILE);


    public static final Item MBTW_GRAVEL_PILE = new Item((new FabricItemSettings().group(ItemGroup.MATERIALS)));

    public static final Block MBTW_GRAVEL_SLAB = new FallingSlabBlock(-8356741, FabricBlockSettings.of(Material.SOIL, MaterialColor.DIRT).strength(0.5f).sounds(BlockSoundGroup.GRAVEL));

    public static final Item MBTW_CHISEL_STONE = new ChiselItem(6, 1, -2.8F, ToolMaterials.STONE, new FabricItemSettings().group(ItemGroup.TOOLS));
    public static final Item MBTW_POINTY_STICK = new ChiselItem(10, 1, -2.8F, ToolMaterials.WOOD, new FabricItemSettings().group(ItemGroup.TOOLS));


    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "loose_cobblestone"), MBTW_LOOSE_COBBLESTONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "loose_cobblestone"), new BlockItem(MBTW_LOOSE_COBBLESTONE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "stone"), MBTW_STONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "stone"), new BlockItem(MBTW_STONE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "hard_stone"), MBTW_HARD_STONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "hard_stone"), new BlockItem(MBTW_HARD_STONE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "deep_stone"), MBTW_DEEP_STONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "deep_stone"), new BlockItem(MBTW_DEEP_STONE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.ITEM, new Identifier("mbtw", "loose_stone"), MBTW_LOOSE_STONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "iron_ore_pile"), MBTW_IRON_ORE_PILE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "iron_ore_chunk"), MBTW_IRON_ORE_CHUNK);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "coal_dust_pile"), MBTW_COAL_DUST_PILE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "gravel_pile"), MBTW_GRAVEL_PILE);

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

        Registry.register(Registry.ITEM, new Identifier("mbtw", "chisel_stone"), MBTW_CHISEL_STONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "pointy_stick"), MBTW_POINTY_STICK);
    }
}
