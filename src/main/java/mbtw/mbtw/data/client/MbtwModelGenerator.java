package mbtw.mbtw.data.client;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.GearboxBlock;
import mbtw.mbtw.state.property.MbtwProperties;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;


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

    public void registerAxle(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textureMapOff = MbtwModels.sideEnd(Mbtw.AXLE);
        Identifier offIdentifier = MbtwModels.TEMPLATE_AXLE.upload(Mbtw.AXLE, textureMapOff, blockStateModelGenerator.modelCollector);
        TextureMap textureMapOn = MbtwModels.sideEndOn(Mbtw.AXLE);
        Identifier onIdentifier = MbtwModels.TEMPLATE_AXLE.upload(Mbtw.AXLE, "_on", textureMapOn, blockStateModelGenerator.modelCollector);
        TextureMap textureMapOnFast = MbtwModels.sideEndOnFast(Mbtw.AXLE);
        Identifier onFastIdentifier = MbtwModels.TEMPLATE_AXLE.upload(Mbtw.AXLE, "_on_fast", textureMapOnFast, blockStateModelGenerator.modelCollector);

        // Note that when you put anything into these variants you also change the original variant
        // Use BlockStateVariant.union() instead if you want to create variants of the variants
        BlockStateVariant baseVariantOff = BlockStateVariant.create().put(VariantSettings.MODEL, offIdentifier);
        BlockStateVariant baseVariantOn = BlockStateVariant.create().put(VariantSettings.MODEL, onIdentifier);
        BlockStateVariant baseVariantFast = BlockStateVariant.create().put(VariantSettings.MODEL, onFastIdentifier);

        When offCondition = When.create().set(MbtwProperties.MECHANICAL_SOURCE, 0);
        When onCondition = intClosedOr(MbtwProperties.MECHANICAL_SOURCE, 1, 2);
        When onFastCondition = intClosedOr(MbtwProperties.MECHANICAL_SOURCE, 3, MbtwProperties.MAX_MECHANICAL_POWER);

        List<BlockStateVariant> variants = List.of(baseVariantOff, baseVariantOn, baseVariantFast);
        List<When> conditions = List.of(offCondition, onCondition, onFastCondition);
        blockStateModelGenerator.blockStateCollector.accept(axisRotations(variants, conditions, true));
    }

    public static BlockStateVariantMap createSouthDefaultRelativeUpRotations() {
        return BlockStateVariantMap.create(Properties.FACING, MbtwProperties.UP_DIRECTION).register((facing, upDirection) -> {
            switch (facing) {
                case DOWN -> {
                    BlockStateVariant xVariant = BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90);
                    return switch (upDirection) {
                        case NORTH -> xVariant;
                        case EAST -> xVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90);
                        case SOUTH -> xVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180);
                        case WEST -> xVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270);
                        default -> throw new UnsupportedOperationException("Fix you generator!");
                    };
                }
                case UP -> {
                    BlockStateVariant xVariant = BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270);
                    return switch (upDirection) {
                        case NORTH -> xVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180);
                        case EAST -> xVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270);
                        case SOUTH -> xVariant;
                        case WEST -> xVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90);
                        default -> throw new UnsupportedOperationException("Fix you generator!");
                    };
                }
                case NORTH -> {
                    return BlockStateVariant.create();
                }
                case SOUTH -> {
                    return BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180);
                }
                case WEST -> {
                    return BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270);
                }
                case EAST -> {
                    return BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90);
                }
            }
            throw new UnsupportedOperationException("Fix you generator!");
        });
    }

    public void registerGearbox(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textureMap = MbtwModels.sideInputOutput(Mbtw.GEARBOX);
        Identifier identifier = MbtwModels.TEMPLATE_GEARBOX.upload(Mbtw.GEARBOX, textureMap, blockStateModelGenerator.modelCollector);
        Identifier identifierOther = MbtwModels.TEMPLATE_GEARBOX_OTHER.upload(Mbtw.GEARBOX, "_other", textureMap, blockStateModelGenerator.modelCollector);

        BlockStateVariantMap rotations = createSouthDefaultRelativeUpRotations();
        VariantsBlockStateSupplier supplier = VariantsBlockStateSupplier.create(Mbtw.GEARBOX).coordinate(BlockStateModelGenerator.createBooleanModelMap(GearboxBlock.MODE, identifierOther, identifier));
        blockStateModelGenerator.blockStateCollector.accept(supplier.coordinate(rotations));
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerMillstone(blockStateModelGenerator);
        registerAxle(blockStateModelGenerator);
        registerGearbox(blockStateModelGenerator);
        blockStateModelGenerator.registerSimpleCubeAll(Mbtw.INFINITE_CRANK);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(Mbtw.FLOUR, Models.GENERATED);
        itemModelGenerator.register(Mbtw.SOUL_FLUX, Models.GENERATED);
        itemModelGenerator.register(Mbtw.SOUL_URN, Models.GENERATED);
        itemModelGenerator.register(Mbtw.SOULFORGED_GOLD, Models.GENERATED);
    }

    // Ensure variants and conditions are of same length
    // This assumes the original model is oriented in the y-axis
    public static MultipartBlockStateSupplier axisRotations(List<BlockStateVariant> variants, List<When> conditions, boolean hasAxisDirection) {
        MultipartBlockStateSupplier supplier = MultipartBlockStateSupplier.create(Mbtw.AXLE);

        When yAxis = When.create().set(Properties.AXIS, Direction.Axis.Y);
        When zAxis = When.create().set(Properties.AXIS, Direction.Axis.Z);
        When xAxis = When.create().set(Properties.AXIS, Direction.Axis.X);

        Iterator<BlockStateVariant> variantIterator = variants.iterator();
        Iterator<When> conditionIterator = conditions.iterator();

        BlockStateVariant x90 = BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90);

        while (variantIterator.hasNext() && conditionIterator.hasNext()) {
            BlockStateVariant variant = variantIterator.next();
            When condition = conditionIterator.next();
            BlockStateVariant xVariant = BlockStateVariant.union(variant, x90);
            if (hasAxisDirection) {
                BlockStateVariant x180 = BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180);
                BlockStateVariant y180 = BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180);
                BlockStateVariant y90 = BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90);

                When axisPos = When.create().set(MbtwProperties.AXIS_DIRECTION, true);
                When axisNeg = When.create().set(MbtwProperties.AXIS_DIRECTION, false);
                BlockStateVariant y270 = BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90);
                supplier = supplier
                        .with(
                                When.allOf(condition, yAxis, axisPos),
                                variant
                        )
                        .with(
                                When.allOf(condition, yAxis, axisNeg),
                                BlockStateVariant.union(variant, x180)
                        )
                        .with(
                                When.allOf(condition, xAxis, axisPos),
                                BlockStateVariant.union(xVariant, y90)
                        )
                        .with(
                                When.allOf(condition, xAxis, axisNeg),
                                BlockStateVariant.union(xVariant, y270)
                        )
                        .with(
                                When.allOf(condition, zAxis, axisPos),
                                xVariant
                        )
                        .with(
                                When.allOf(condition, zAxis, axisNeg),
                                BlockStateVariant.union(xVariant, y180)
                        );
            } else {
                supplier = supplier
                        .with(
                                When.allOf(condition, yAxis),
                                variant
                        )
                        .with(
                                When.allOf(condition, zAxis),
                                xVariant
                        )
                        .with(
                                When.allOf(condition, xAxis),
                                xVariant
                        );
            }
        }
        return supplier;
    }

    public static When intClosedOr(IntProperty property, int minValue, int maxValue) {
        // We need Integer[] instead of int[] because otherwise it cannot be passed as varargs
        Integer[] integerArgs = IntStream.rangeClosed(minValue+1, maxValue).boxed().toArray(Integer[]::new);

        return When.create().set(property, minValue, integerArgs);
    }
}
