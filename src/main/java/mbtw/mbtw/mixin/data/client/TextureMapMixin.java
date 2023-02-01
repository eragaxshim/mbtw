package mbtw.mbtw.mixin.data.client;

import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin(TextureMap.class)
public abstract class TextureMapMixin implements TextureMapAccessor {
    @Shadow @Final private Map<TextureKey, Identifier> entries;

    @Shadow @Final private Set<TextureKey> inherited;

    @Override
    public void setEntries(Map<TextureKey, Identifier> newEntries) {
        this.entries.clear();
        this.entries.putAll(newEntries);
    }

    @Override
    public void setInherited(Set<TextureKey> newInherited) {
        this.inherited.clear();
        this.inherited.addAll(newInherited);
    }

}
