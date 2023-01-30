package mbtw.mbtw.client.render.block.entity;

import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class VariableCampfireBlockEntityRenderer implements BlockEntityRenderer<VariableCampfireBlockEntity> {
    public VariableCampfireBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    public void render(VariableCampfireBlockEntity campfireBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Direction direction = campfireBlockEntity.getCachedState().get(CampfireBlock.FACING);
        DefaultedList<ItemStack> defaultedList = campfireBlockEntity.getItemsBeingCooked();
        int l = (int)campfireBlockEntity.getPos().asLong();
        for(int k = 0; k < defaultedList.size(); ++k) {
            ItemStack itemStack = defaultedList.get(k);
            if (itemStack != ItemStack.EMPTY) {
                matrixStack.push();
                matrixStack.translate(0.5D, 0.44921875D, 0.5D);
                int d = k == 0 ? 1 : -1;
                matrixStack.translate(direction.getOffsetX()*0.3375D*d, 0D, direction.getOffsetZ()*0.3375D*d);
                Direction direction2 = Direction.fromHorizontal((k + direction.getHorizontal()) % 4);
                float g = -direction2.asRotation();
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(k*90.0F));
                matrixStack.scale(0.375F, 0.375F, 0.375F);
                // TODO check if seed is used correctly
                MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.FIXED, i, j, matrixStack, vertexConsumerProvider, k + l);
                matrixStack.pop();
            }
        }

    }
}
