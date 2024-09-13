package btw.mbtw.block;

import btw.mbtw.Mbtw;
import btw.mbtw.block.entity.BrickOvenBlockEntity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static net.minecraft.block.Blocks.createLightLevelFromLitBlockState;

public class MbtwBlocks {
    public static final Block BRICK_OVEN = register(
            new BrickOvenBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.RED)
                    .requiresTool()
                    .strength(2.0F, 6.0F)
                    .luminance(createLightLevelFromLitBlockState(13))),
            "brick_oven",
            true
    );

    public static BlockEntityType<BrickOvenBlockEntity> BRICK_OVEN_ENTITY = registerBlockEntity(BRICK_OVEN, BrickOvenBlockEntity::new, "brick_oven");

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(Block block, BlockEntityType.BlockEntityFactory<T> factory, String name) {
        Identifier id = Identifier.of(Mbtw.MOD_ID, name);

        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.create(factory, block).build(null));
    }

    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        Identifier id = Identifier.of(Mbtw.MOD_ID, name);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(Mbtw.MBTW_ITEM_GROUP_KEY).register((itemGroup) -> {
            itemGroup.add(BRICK_OVEN.asItem());
        });


    }
}
