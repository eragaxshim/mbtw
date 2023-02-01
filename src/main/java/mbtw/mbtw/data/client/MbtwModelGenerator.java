package mbtw.mbtw.data.client;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.state.property.MbtwProperties;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

public class MbtwModelGenerator extends FabricModelProvider {
    public MbtwModelGenerator(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textureMapOff = TextureMap.sideTopBottom(Mbtw.MILLSTONE);
        Identifier offIdentifier = MbtwModels.TEMPLATE_MILLSTONE.upload(Mbtw.MILLSTONE, textureMapOff, blockStateModelGenerator.modelCollector);
        TextureMap textureMapOn = MbtwModels.addSuffixToCopy(textureMapOff, "_on");
        Identifier onIdentifier = MbtwModels.TEMPLATE_MILLSTONE.upload(Mbtw.MILLSTONE, "_on", textureMapOn, blockStateModelGenerator.modelCollector);


        blockStateModelGenerator.blockStateCollector.accept(MultipartBlockStateSupplier.create(Mbtw.MILLSTONE)
                .with(When.create().set(MbtwProperties.POWERED, false),
                        BlockStateVariant.create().put(VariantSettings.MODEL, offIdentifier))
                .with(When.create().set(MbtwProperties.POWERED, true),
                        BlockStateVariant.create().put(VariantSettings.MODEL, onIdentifier))
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}
