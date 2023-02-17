package mbtw.mbtw.render.block.entity;

import mbtw.mbtw.block.entity.MechanicalHopperBlockEntity;
import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class MechanicalHopperBlockEntityRenderer implements BlockEntityRenderer<MechanicalHopperBlockEntity> {
    private final BlockRenderManager renderManager;


    public MechanicalHopperBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.renderManager = ctx.getRenderManager();
    }

    @Override
    public void render(MechanicalHopperBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState block = entity.getFilterModel();
        if (block == null || block.isOf(Blocks.AIR)) {
            return;
        }
        // It starts in bottom corner, the scaled version needs to be moved 2px in x and z to be centered and up to be
        // above the hopper bottom
        matrices.translate(0.125F, 0.8F, 0.125F);
        // Scaled to 12/16th in x and z and to 1/16th in y, so it is just a thin slice
        matrices.scale(0.75F, 0.0625F, 0.75F);


        renderManager.renderBlockAsEntity(block, matrices, vertexConsumers, light, overlay);
    }
}
