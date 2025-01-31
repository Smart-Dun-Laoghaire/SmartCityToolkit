package org.example.smart.smartcitytoolkitforge1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IMUBlockEntity extends BlockEntity {
    private float rotationX = 0;
    private float rotationY = 0;
    private float rotationZ = 0;
    private float positionX = 0;
    private float positionY = 0;
    private float positionZ = 0;

   public IMUBlockEntity(BlockPos pos, BlockState state) {
       super(Smartcitytoolkitforge1.IMU_BLOCK_ENTITY_TYPE.get(), pos, state);
       final Sensor linkedSensor;
   }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        // Load saved rotation and position data
        rotationX = tag.getFloat("rotationX");
        rotationY = tag.getFloat("rotationY");
        rotationZ = tag.getFloat("rotationZ");
        positionX = tag.getFloat("positionX");
        positionY = tag.getFloat("positionY");
        positionZ = tag.getFloat("positionZ");
    }
    @Override
    protected void saveAdditional(CompoundTag tag,  HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        // Save rotation and position data
        tag.putFloat("rotationX", rotationX);
        tag.putFloat("rotationY", rotationY);
        tag.putFloat("rotationZ", rotationZ);
        tag.putFloat("positionX", positionX);
        tag.putFloat("positionY", positionY);
        tag.putFloat("positionZ", positionZ);
    }

    public void tick() {
        if (level != null && !level.isClientSide) {
            IMUSensor IMUSensor = null;
            if (IMUSensor != null) {
                rotationX = IMUSensor.getRotationX();
                rotationY = IMUSensor.getRotationY();
                rotationZ = IMUSensor.getRotationZ();
                positionX = IMUSensor.getPositionX();
                positionY = IMUSensor.getPositionY();
                positionZ = IMUSensor.getPositionZ();
                setChanged(); // Mark the block entity as dirty to save changes
            }
        }
    }

    public void setRotation(float rotationX, float rotationY, float rotationZ) {
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
    }

    public void setPosition(float positionX, float positionY, float positionZ) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
    }

    @OnlyIn(Dist.CLIENT)
    public void animate() {
        // This method is called on the client side to animate the block
        // Use the rotation and position values to modify the block's appearance
        Level level = this.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        // Example: Rotate the block based on IMU data
        BlockPos pos = this.getBlockPos();
        BlockState state = level.getBlockState(pos);

        // Trigger a re-render of the block
        level.sendBlockUpdated(pos, state, state, 3);
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public float getPositionX() {
        return positionX;
    }
    public float getPositionY() {
        return positionY;
    }

    public float getPositionZ() {
        return positionZ;
    }
    /*
    public void move() {
           if (this.level instanceof ServerLevel serverLevel) {
               Direction facing = Direction.NORTH; // Set default direction or get from some logic
               BlockPos targetPos = this.worldPosition.relative(facing);

               if (serverLevel.isEmptyBlock(targetPos)) {
                   BlockState currentState = this.getBlockState();

                   // Remove current block
                   serverLevel.setBlock(this.worldPosition, Blocks.AIR.defaultBlockState(), 3);

                   // Set block at new position
                   serverLevel.setBlock(targetPos, currentState, 3);

                   // Update block entity position
                   this.setIMUBlockEntity(targetPos);
               }
           }
       }
    */
    private void setIMUBlockEntity(BlockPos newPos) {
       if (this.level instanceof ServerLevel serverLevel) {
           BlockEntity newEntity = serverLevel.getBlockEntity(newPos);
           if (newEntity instanceof IMUBlockEntity IMUBlockEntity) {

           }
       }
   }

}