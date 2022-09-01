package mbtw.mbtw.client;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.client.gui.screen.ingame.BrickOvenScreen;
import mbtw.mbtw.client.render.block.entity.VariableCampfireBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.LightBlock;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MbtwClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(Mbtw.BRICK_OVEN_SCREEN_HANDLER, BrickOvenScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Mbtw.DAMAGED_COBWEB);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Mbtw.VARIABLE_CAMPFIRE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Mbtw.FINITE_TORCH);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Mbtw.FINITE_WALL_TORCH);
        BlockEntityRendererRegistry.register(Mbtw.VARIABLE_CAMPFIRE_ENTITY, VariableCampfireBlockEntityRenderer::new);
        ModelPredicateProviderRegistry.register(Mbtw.FINITE_TORCH_ITEM, new Identifier("torch_size"), (stack, world, entity, seed) -> {
            NbtCompound nbtCompound = stack.getSubNbt("BlockStateTag");
            if (nbtCompound != null) {
                int torch_fire = nbtCompound.getInt("torch_fire");
                return switch (torch_fire) {
                    case 1 -> 0.33f;
                    case 2 -> 0.66f;
                    case 3 -> 1f;
                    default -> 0f;
                };
            }
            return 0f;
        });
    }
}
