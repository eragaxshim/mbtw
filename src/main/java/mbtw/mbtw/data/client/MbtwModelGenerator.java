package mbtw.mbtw.data.client;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.state.property.MbtwProperties;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class MbtwModelGenerator extends FabricModelProvider {
    public MbtwModelGenerator(FabricDataOutput generator) {
        super(generator);
    }


    public void registerMillstone(BlockStateModelGenerator blockStateModelGenerator) {
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

    private static Identifier sourceToModel(int source, Identifier offIdentifier, Identifier onIdentifier, Identifier onFastIdentifier) {
        if (source == 0) {
            return offIdentifier;
        } else if (source < 3) {
            return onIdentifier;
        } else {
            return onFastIdentifier;
        }
    }

    public void registerAxle(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textureMapOff = MbtwModels.sideEnd(Mbtw.AXLE);
        Identifier offIdentifier = MbtwModels.TEMPLATE_AXLE.upload(Mbtw.AXLE, textureMapOff, blockStateModelGenerator.modelCollector);
        TextureMap textureMapOn = MbtwModels.sideEndOn(Mbtw.AXLE);
        Identifier onIdentifier = MbtwModels.TEMPLATE_AXLE.upload(Mbtw.AXLE, "_on", textureMapOn, blockStateModelGenerator.modelCollector);
        TextureMap textureMapOnFast = MbtwModels.sideEndOnFast(Mbtw.AXLE);
        Identifier onFastIdentifier = MbtwModels.TEMPLATE_AXLE.upload(Mbtw.AXLE, "_on_fast", textureMapOnFast, blockStateModelGenerator.modelCollector);

        BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(MbtwProperties.MECHANICAL_SOURCE, Properties.AXIS, MbtwProperties.AXIS_DIRECTION).register((source, axis, axis_direction) -> {
                switch (axis) {
                    case Y -> {
                        BlockStateVariant baseVariant = BlockStateVariant.create().put(VariantSettings.MODEL, sourceToModel(source, offIdentifier, onIdentifier, onFastIdentifier));
                        return axis_direction ? baseVariant : baseVariant.put(VariantSettings.X, VariantSettings.Rotation.R180);
                    }
                    case Z -> {
                        BlockStateVariant baseVariant = BlockStateVariant.create().put(VariantSettings.MODEL, sourceToModel(source, offIdentifier, onIdentifier, onFastIdentifier)).put(VariantSettings.X, VariantSettings.Rotation.R90);
                        return axis_direction ? baseVariant : baseVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180);
                    }
                    case X -> {
                        BlockStateVariant baseVariant = BlockStateVariant.create().put(VariantSettings.MODEL, sourceToModel(source, offIdentifier, onIdentifier, onFastIdentifier)).put(VariantSettings.X, VariantSettings.Rotation.R90);
                        return axis_direction ? baseVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90) : baseVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270);
                    }
                }
            throw new UnsupportedOperationException("Fix you generator!");
        });

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(Mbtw.AXLE).coordinate(blockStateVariantMap));
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerMillstone(blockStateModelGenerator);
        registerAxle(blockStateModelGenerator);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(Mbtw.FLOUR, Models.GENERATED);
    }
}
