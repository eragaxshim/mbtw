package mbtw.mbtw.data.client;

import mbtw.mbtw.Mbtw;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

import java.util.*;

public class MbtwModels {
    public static final TextureKey INPUT = TextureKey.of("input", TextureKey.ALL);
    public static final TextureKey OUTPUT = TextureKey.of("output", TextureKey.ALL);

    public static final Model TEMPLATE_MILLSTONE = MbtwModels.block("template_millstone", TextureKey.TOP, TextureKey.SIDE, TextureKey.BOTTOM);
    public static final Model TEMPLATE_AXLE = MbtwModels.block("template_axle", TextureKey.SIDE, TextureKey.END);
    public static final Model TEMPLATE_GEARBOX = MbtwModels.block("template_gearbox", TextureKey.SIDE, INPUT, OUTPUT);
    public static final Model TEMPLATE_GEARBOX_OTHER = MbtwModels.block("template_gearbox_other", TextureKey.SIDE, INPUT, OUTPUT);
    public static final Model TEMPLATE_VESSEL = MbtwModels.block("template_vessel", TextureKey.SIDE, TextureKey.INSIDE, TextureKey.BOTTOM, TextureKey.TOP, TextureKey.CONTENT);

    private static Model block(String parent, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(new Identifier(Mbtw.MOD_ID, "block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    public static TextureMap addSuffixToCopy(TextureMap textureMap, String suffix) {

        Set<TextureKey> inherited = ((TextureMapMixinAccessor) textureMap).getInherited();
        Map<TextureKey, Identifier> entries = ((TextureMapMixinAccessor) textureMap).getEntries();

        Set<TextureKey> newInherited = new HashSet<>(inherited);
        Map<TextureKey, Identifier> newEntries = new HashMap<>();

        for (Map.Entry<TextureKey, Identifier> entry : entries.entrySet()) {
            Identifier newIdentifer = entry.getValue().withPath(path -> path + suffix);
            newEntries.put(entry.getKey(), newIdentifer);
        }
        TextureMap newTextureMap = new TextureMap();
        ((TextureMapMixinAccessor) newTextureMap).setInherited(newInherited);
        ((TextureMapMixinAccessor) newTextureMap).setEntries(newEntries);
        return newTextureMap;
    }

    public static TextureMap sideEnd(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.END, TextureMap.getSubId(block, "_end"));
    }

    public static TextureMap sideEndOn(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side_on")).put(TextureKey.END, TextureMap.getSubId(block, "_end"));
    }

    public static TextureMap sideEndOnFast(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side_on_fast")).put(TextureKey.END, TextureMap.getSubId(block, "_end"));
    }

    public static TextureMap sideInputOutput(Block block) {
        return new TextureMap().put(INPUT, TextureMap.getSubId(block, "_input")).put(OUTPUT, TextureMap.getSubId(block, "_output")).put(TextureKey.SIDE, TextureMap.getSubId(block, "_side"));
    }
    
    public static TextureMap vessel(Block block) {
        return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.TOP, TextureMap.getSubId(block, "_top")).put(TextureKey.BOTTOM, TextureMap.getSubId(block, "_bottom")).put(TextureKey.INSIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.CONTENT, TextureMap.getSubId(block, "_content"));
    }
}
