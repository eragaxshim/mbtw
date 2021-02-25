package mbtw.mbtw.tag;

import mbtw.mbtw.Mbtw;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class MbtwTags {
    public static final Tag<Block> DEEP_STONE_OVERWORLD = registerBlockTag("deep_stone_overworld");
    public static final Tag<Block> BREAK_INTERCEPTABLES = registerBlockTag("break_interceptables");
    public static final Tag<Block> TRUNKS = registerBlockTag("trunks");

    public static final Tag<Item> CHISELS = registerItemTag("chisels");

    private MbtwTags() { }

    private static Tag<Item> registerItemTag(String id) {
        return TagRegistry.item(new Identifier(Mbtw.MOD_ID, id));
    }

    private static Tag<Block> registerBlockTag(String id) {
        return TagRegistry.block(new Identifier(Mbtw.MOD_ID, id));
    }
}
