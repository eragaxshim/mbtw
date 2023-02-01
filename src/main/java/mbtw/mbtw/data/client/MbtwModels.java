package mbtw.mbtw.data.client;

import mbtw.mbtw.Mbtw;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

import java.util.*;

public class MbtwModels {
    public static final Model TEMPLATE_MILLSTONE = MbtwModels.block("template_millstone", TextureKey.TOP, TextureKey.SIDE, TextureKey.BOTTOM);

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
}
