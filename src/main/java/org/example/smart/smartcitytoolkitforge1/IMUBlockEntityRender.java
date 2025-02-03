package org.example.smart.smartcitytoolkitforge1;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.example.smart.smartcitytoolkitforge1.IMUBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class IMUBlockEntityRender implements BlockEntityRenderer<IMUBlockEntity>{

    public IMUBlockEntityRender(BlockEntityRendererProvider.Context context) {
        // Constructor for the renderer
    }
    @Override
    public void render(IMUBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        int blockLight = LightTexture.pack(15, 15);

        float yaw = blockEntity.getRotationX();
        float pitch = blockEntity.getRotationY();
        float roll = blockEntity.getRotationZ();

        int LU = blockEntity.getLight_UP();
        int LD = blockEntity.getLight_DOWN();
        int LF = blockEntity.getLight_FORWARD();
        int LB = blockEntity.getLight_BACK();
        int LL = blockEntity.getLight_LEFT();
        int LR = blockEntity.getLight_RIGHT();

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.mulPose(Axis.ZP.rotationDegrees(roll));

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        BakedModel model = dispatcher.getBlockModel(blockEntity.getBlockState());

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());

        // FORWARD
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LF, LF, LF, Direction.NORTH);

        // BACK
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LB, LB, LB, Direction.SOUTH);

        // LEFT
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LL, LL, LL, Direction.WEST);

        // RIGHT
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LR, LR, LR, Direction.EAST);

        // UP
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LU, LU, LU, Direction.UP);

        // DOWN
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LD, LD, LD, Direction.DOWN);

        poseStack.popPose();
    }

    private void renderFaceWithTint(BlockRenderDispatcher dispatcher, VertexConsumer vertexConsumer, PoseStack poseStack,
                                    BlockState state, BakedModel model, int light, int overlay,
                                    int r, int g, int b, Direction direction) {
        List<BakedQuad> quads = model.getQuads(state, direction, RandomSource.create());

        for (BakedQuad quad : quads) {
            vertexConsumer.putBulkData(poseStack.last(), quad, r / 255f, g / 255f, b / 255f, 1.0f, light, overlay);
        }
    }
}
