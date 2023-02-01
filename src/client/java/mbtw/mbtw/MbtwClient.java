package mbtw.mbtw;

import mbtw.mbtw.gui.screen.ingame.BrickOvenScreen;
import mbtw.mbtw.render.block.entity.VariableCampfireBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class MbtwClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(Mbtw.BRICK_OVEN_SCREEN_HANDLER, BrickOvenScreen::new);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Mbtw.DAMAGED_COBWEB);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Mbtw.VARIABLE_CAMPFIRE);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Mbtw.FINITE_TORCH);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Mbtw.FINITE_WALL_TORCH);
		BlockEntityRendererFactories.register(Mbtw.VARIABLE_CAMPFIRE_ENTITY, VariableCampfireBlockEntityRenderer::new);
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
