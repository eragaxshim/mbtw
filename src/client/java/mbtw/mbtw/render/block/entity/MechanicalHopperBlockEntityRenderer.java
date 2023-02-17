package mbtw.mbtw.render.block.entity;

import mbtw.mbtw.block.entity.MechanicalHopperBlockEntity;
import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
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
        matrices.translate(0.125F, 0.8F, 0.125F);
        matrices.scale(0.75F, 0.0625F, 0.75F);


        renderManager.renderBlockAsEntity(block, matrices, vertexConsumers, light, overlay);
    }
}
