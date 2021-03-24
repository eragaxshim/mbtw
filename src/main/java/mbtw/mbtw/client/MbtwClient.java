package mbtw.mbtw.client;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import mbtw.mbtw.client.gui.screen.ingame.BrickOvenScreen;
import mbtw.mbtw.client.render.block.entity.VariableCampfireBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class MbtwClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(Mbtw.BRICK_OVEN_SCREEN_HANDLER, BrickOvenScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Mbtw.DAMAGED_COBWEB);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Mbtw.VARIABLE_CAMPFIRE);
        BlockEntityRendererRegistry.INSTANCE.register(Mbtw.VARIABLE_CAMPFIRE_ENTITY, VariableCampfireBlockEntityRenderer::new);
    }
}
