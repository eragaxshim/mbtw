package mbtw.mbtw.data.client;

import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;

public interface TextureMapMixinAccessor {
    void setEntries(Map<TextureKey, Identifier> newEntries);

    void setInherited(Set<TextureKey> newEntries);

    Map<TextureKey, Identifier> getEntries();

    Set<TextureKey> getInherited();
}
