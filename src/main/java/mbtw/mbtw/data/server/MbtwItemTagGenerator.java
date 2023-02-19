package mbtw.mbtw.data.server;

import mbtw.mbtw.tag.MbtwTagsMaps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MbtwItemTagGenerator extends FabricTagProvider.ItemTagProvider {

    public MbtwItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.copy(MbtwTagsMaps.SOUL_FILTERS_BLOCKS, MbtwTagsMaps.SOUL_FILTERS);
        this.getOrCreateTagBuilder(MbtwTagsMaps.SOUL_FILTERABLE).add(Items.NETHERRACK);
    }
}

