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
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class IMUBlockEntityRender implements BlockEntityRenderer<IMUBlockEntity>{

    public IMUBlockEntityRender(BlockEntityRendererProvider.Context context) {
        // Constructor for the renderer
    }
    @Override
    public void render(IMUBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        int blockLight = LightTexture.pack(15, 15);

        float positionX = blockEntity.getPositionX();
        float positionY = blockEntity.getPositionY();
        float positionZ = blockEntity.getPositionZ();

        float roll = blockEntity.getRotationX();
        float pitch = blockEntity.getRotationY();
        float yaw = blockEntity.getRotationZ();

        int LU = blockEntity.getLight_UP();
        int LD = blockEntity.getLight_DOWN();
        int LF = blockEntity.getLight_FORWARD();
        int LB = blockEntity.getLight_BACK();
        int LL = blockEntity.getLight_LEFT();
        int LR = blockEntity.getLight_RIGHT();


        Quaternionf rotation = new Quaternionf();
        rotation.rotateY((float) Math.toRadians(yaw));  // Apply yaw rotation
        rotation.rotateX((float) Math.toRadians(pitch));  // Apply pitch rotation
        rotation.rotateZ((float) Math.toRadians(roll));  // Apply roll rotation

        float pivotX = 0.5f + positionX;
        float pivotY = 0.5f + positionY;
        float pivotZ = 0.5f + positionZ;

        poseStack.rotateAround(rotation, pivotX, pivotY, pivotZ);  // Apply rotation around the pivot



        // POSITION / MOVING
        poseStack.translate(positionX, positionY, positionZ);




        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        BakedModel model = dispatcher.getBlockModel(blockEntity.getBlockState());

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());

        // FORWARD
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LF, Direction.NORTH);

        // BACK
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LB, Direction.SOUTH);

        // LEFT
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LL, Direction.WEST);

        // RIGHT
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LR, Direction.EAST);

        // UP
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LU, Direction.UP);

        // DOWN
        renderFaceWithTint(dispatcher, vertexConsumer, poseStack, blockEntity.getBlockState(), model, blockLight, packedOverlay, LD, Direction.DOWN);

        poseStack.popPose();
    }
    private void renderFaceWithTint(BlockRenderDispatcher dispatcher, VertexConsumer vertexConsumer, PoseStack poseStack,
                                    BlockState state, BakedModel model, int light, int overlay,
                                    int lightColor, Direction direction) {
        List<BakedQuad> quads = model.getQuads(state, direction, RandomSource.create());

        for (BakedQuad quad : quads) {
            vertexConsumer.putBulkData(poseStack.last(), quad, lightColor / 255f, lightColor / 255f, lightColor / 255f, 1.0f, light, overlay);
        }
    }
}
