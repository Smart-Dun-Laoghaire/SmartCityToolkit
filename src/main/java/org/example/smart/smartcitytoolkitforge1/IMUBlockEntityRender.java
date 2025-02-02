package org.example.smart.smartcitytoolkitforge1;
import org.example.smart.smartcitytoolkitforge1.IMUBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public class IMUBlockEntityRender implements BlockEntityRenderer<IMUBlockEntity>{

    public IMUBlockEntityRender(BlockEntityRendererProvider.Context context) {
        // Constructor for the renderer
    }
    @Override
    public void render(IMUBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        blockEntity.move(blockEntity);

        /*poseStack.pushPose();

        // Translate to the center of the block
        poseStack.translate(0.5, 0.5, 0.5);

        // Apply rotations
        float yaw = blockEntity.getRotationX();
        float pitch = blockEntity.getRotationY();
        float roll = blockEntity.getRotationZ();

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.mulPose(Axis.ZP.rotationDegrees(roll));

        // Translate back to the corner of the block
        poseStack.translate(-0.5, -0.5, -0.5);

        // Render the block
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockEntity.getBlockState(), poseStack, bufferSource, packedLight, packedOverlay);

        poseStack.popPose();*/
    }
}
