package mbtw.mbtw.data.server;

import mbtw.mbtw.tag.MbtwTagsMaps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MbtwBlockTagGenerator extends FabricTagProvider.BlockTagProvider {


    public MbtwBlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.getOrCreateTagBuilder(MbtwTagsMaps.SOUL_FILTERS_BLOCKS).add(Blocks.SOUL_SAND);
    }
}
