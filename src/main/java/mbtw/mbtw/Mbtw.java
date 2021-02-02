package mbtw.mbtw;

import mbtw.mbtw.block.MultiBreakBlock;
import mbtw.mbtw.block.StratifiedBlock;
import mbtw.mbtw.item.PointyStickItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Mbtw implements ModInitializer {
    public static final Item MBTW_POINTY_STICK = new PointyStickItem(new FabricItemSettings().group(ItemGroup.TOOLS));
    public static final Block MBTW_LOOSE_COBBLESTONE = new Block(FabricBlockSettings.of(Material.STONE).strength(1.0f));
    public static final Block MBTW_STONE = new StratifiedBlock(FabricBlockSettings.of(Material.STONE).strength(1.0f), 5);

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("mbtw", "pointy_stick"), MBTW_POINTY_STICK);
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "loose_cobblestone"), MBTW_LOOSE_COBBLESTONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "loose_cobblestone"), new BlockItem(MBTW_LOOSE_COBBLESTONE, new FabricItemSettings().group(ItemGroup.MISC)));
        Registry.register(Registry.BLOCK, new Identifier("mbtw", "stone"), MBTW_STONE);
        Registry.register(Registry.ITEM, new Identifier("mbtw", "stone"), new BlockItem(MBTW_STONE, new FabricItemSettings().group(ItemGroup.MISC)));
    }
}
