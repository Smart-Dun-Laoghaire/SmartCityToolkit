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


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class IMUBlockEntity extends BlockEntity {
    private float rotationX = 0;
    private float rotationY = 0;
    private float rotationZ = 0;
    private float positionX = 0;
    private float positionY = 0;
    private float positionZ = 0;

    private int light_UP = 0;
    private int light_DOWN = 0;
    private int light_FORWARD = 0;
    private int light_BACK = 0;
    private int light_LEFT = 0;
    private int light_RIGHT = 0;
    private IMUSensor linkedSensor = null;
    private static final Logger LOGGER = LogManager.getLogger();

    public IMUBlockEntity(BlockPos pos, BlockState state) {
       super(Smartcitytoolkitforge1.IMU_BLOCK_ENTITY_TYPE.get(), pos, state);
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
        light_UP = tag.getInt("light_UP");
        light_DOWN = tag.getInt("light_DOWN");
        light_FORWARD = tag.getInt("light_FORWARD");
        light_BACK = tag.getInt("light_BACK");
        light_LEFT = tag.getInt("light_LEFT");
        light_RIGHT = tag.getInt("light_RIGHT");
    }
    @Override
    protected void saveAdditional(CompoundTag tag,  HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        // Save rotation, light and position data
        tag.putFloat("rotationX", rotationX);
        tag.putFloat("rotationY", rotationY);
        tag.putFloat("rotationZ", rotationZ);
        tag.putFloat("positionX", positionX);
        tag.putFloat("positionY", positionY);
        tag.putFloat("positionZ", positionZ);
        tag.putInt("light_UP", light_UP);
        tag.putInt("light_DOWN", light_DOWN);
        tag.putInt("light_FORWARD", light_FORWARD);
        tag.putInt("light_BACK", light_BACK);
        tag.putInt("light_LEFT", light_LEFT);
        tag.putInt("light_RIGHT", light_RIGHT);
    }

    public void tick() {
        if (level != null && !level.isClientSide) {
            IMUSensor IMUSensor = linkedSensor;
            if (IMUSensor != null) {
                rotationX = IMUSensor.getRotationX();
                rotationY = IMUSensor.getRotationY();
                rotationZ = IMUSensor.getRotationZ();
                positionX = IMUSensor.getPositionX();
                positionY = IMUSensor.getPositionY();
                positionZ = IMUSensor.getPositionZ();
                light_UP = IMUSensor.getLight_UP();
                light_DOWN = IMUSensor.getLight_DOWN();
                light_FORWARD = IMUSensor.getLight_FORWARD();
                light_BACK = IMUSensor.getLight_BACK();
                light_LEFT = IMUSensor.getLight_LEFT();
                light_RIGHT = IMUSensor.getLight_RIGHT();
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

    public void setLight(int light_UP, int light_DOWN, int light_FORWARD, int light_BACK, int light_LEFT, int light_RIGHT) {
        this.light_UP = light_UP;
        this.light_DOWN = light_DOWN;
        this.light_FORWARD = light_FORWARD;
        this.light_BACK = light_BACK;
        this.light_LEFT = light_LEFT;
        this.light_RIGHT = light_RIGHT;
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

    public void setLinkedSensor(IMUSensor sensor) {
        linkedSensor = sensor;
    }

    int count = 0;

    public void move(BlockEntity blockEntity) {


        count++;
        if (count % 10 == 0) {
            if (level != null) {
                LOGGER.info("LEVEL IS NOT NULL");



                BlockPos fromPos1 = blockEntity.getBlockPos();
                BlockPos toPos = new BlockPos(fromPos1.getX() + 1,
                        fromPos1.getY(), fromPos1.getZ());

                BlockState blockState = level.getBlockState(fromPos1);


                if (!blockState.isAir()) {


                    level.removeBlockEntity(fromPos1);
                    level.removeBlock(fromPos1, false);

                    level.setBlock(toPos, blockState, Block.UPDATE_ALL);
                    level.setBlockEntity(blockEntity);


                    /*HolderLookup.Provider lookupProvider = level.registryAccess();
                    CompoundTag nbtData = blockEntity.saveWithFullMetadata(lookupProvider);

                    level.setBlock(toPos, blockState, Block.UPDATE_ALL);
                    BlockEntity newBlockEntity = level.getBlockEntity(toPos);
                    if (newBlockEntity != null) {
                        newBlockEntity.loadWithComponents(nbtData, lookupProvider);
                    }


                    level.sendBlockUpdated(toPos, blockState, blockState, Block.UPDATE_ALL);*/
                }
            }
        }

   }


    private void setIMUBlockEntity(BlockPos newPos) {
       if (this.level instanceof ServerLevel serverLevel) {
           BlockEntity newEntity = serverLevel.getBlockEntity(newPos);
           if (newEntity instanceof IMUBlockEntity IMUBlockEntity) {

           }
       }
   }

    public int getLight_UP() {
        return light_UP;
    }

    public void setLight_UP(int light_UP) {
        this.light_UP = light_UP;
    }

    public int getLight_DOWN() {
        return light_DOWN;
    }

    public void setLight_DOWN(int light_DOWN) {
        this.light_DOWN = light_DOWN;
    }

    public int getLight_FORWARD() {
        return light_FORWARD;
    }

    public void setLight_FORWARD(int light_FORWARD) {
        this.light_FORWARD = light_FORWARD;
    }

    public int getLight_BACK() {
        return light_BACK;
    }

    public void setLight_BACK(int light_BACK) {
        this.light_BACK = light_BACK;
    }

    public int getLight_LEFT() {
        return light_LEFT;
    }

    public void setLight_LEFT(int light_LEFT) {
        this.light_LEFT = light_LEFT;
    }

    public int getLight_RIGHT() {
        return light_RIGHT;
    }

    public void setLight_RIGHT(int light_RIGHT) {
        this.light_RIGHT = light_RIGHT;
    }
}