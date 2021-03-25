package mbtw.mbtw.client.render.block.entity;

import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

public class VariableCampfireBlockEntityRenderer extends BlockEntityRenderer<VariableCampfireBlockEntity> {
    public VariableCampfireBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void render(VariableCampfireBlockEntity campfireBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Direction direction = (Direction)campfireBlockEntity.getCachedState().get(CampfireBlock.FACING);
        DefaultedList<ItemStack> defaultedList = campfireBlockEntity.getItemsBeingCooked();

        for(int k = 0; k < defaultedList.size(); ++k) {
            ItemStack itemStack = (ItemStack)defaultedList.get(k);
            if (itemStack != ItemStack.EMPTY) {
                matrixStack.push();
                matrixStack.translate(0.5D, 0.44921875D, 0.5D);
                int d = k == 0 ? 1 : -1;
                matrixStack.translate(direction.getOffsetX()*0.3375D*d, 0D, direction.getOffsetZ()*0.3375D*d);
                Direction direction2 = Direction.fromHorizontal((k + direction.getHorizontal()) % 4);
                float g = -direction2.asRotation();
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(g));
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(45.0F));
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(k*90.0F));
                matrixStack.scale(0.375F, 0.375F, 0.375F);
                MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.FIXED, i, j, matrixStack, vertexConsumerProvider);
                matrixStack.pop();
            }
        }

    }
}
