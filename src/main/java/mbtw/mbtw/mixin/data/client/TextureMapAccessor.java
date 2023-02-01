package mbtw.mbtw.mixin.data.client;

import mbtw.mbtw.data.client.TextureMapMixinAccessor;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(TextureMap.class)
public interface TextureMapAccessor extends TextureMapMixinAccessor {
    @Accessor("entries")
    Map<TextureKey, Identifier> getEntries();

    @Accessor("inherited")
    Set<TextureKey> getInherited();


}
