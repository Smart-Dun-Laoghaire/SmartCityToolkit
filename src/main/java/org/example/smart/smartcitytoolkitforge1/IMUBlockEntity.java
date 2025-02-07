package org.example.smart.smartcitytoolkitforge1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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

        CompoundTag data = tag.getCompound(Smartcitytoolkitforge1.MODID);

        // Load saved rotation and position data
        this.rotationX = data.getFloat("rotationX");
        this.rotationY = data.getFloat("rotationY");
        this.rotationZ = data.getFloat("rotationZ");
        this.positionX = data.getFloat("positionX");
        this.positionY = data.getFloat("positionY");
        this.positionZ = data.getFloat("positionZ");
        this.light_UP = data.getInt("light_UP");
        this.light_DOWN = data.getInt("light_DOWN");
        this.light_FORWARD = data.getInt("light_FORWARD");
        this.light_BACK = data.getInt("light_BACK");
        this.light_LEFT = data.getInt("light_LEFT");
        this.light_RIGHT = data.getInt("light_RIGHT");
    }

    @Override
    protected void saveAdditional(CompoundTag tag,  HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        var data = new CompoundTag();
        // Save rotation, light and position data
        data.putFloat("rotationX", this.rotationX);
        data.putFloat("rotationY", this.rotationY);
        data.putFloat("rotationZ", this.rotationZ);
        data.putFloat("positionX", this.positionX);
        data.putFloat("positionY", this.positionY);
        data.putFloat("positionZ", this.positionZ);
        data.putInt("light_UP", this.light_UP);
        data.putInt("light_DOWN", this.light_DOWN);
        data.putInt("light_FORWARD", this.light_FORWARD);
        data.putInt("light_BACK", this.light_BACK);
        data.putInt("light_LEFT", this.light_LEFT);
        data.putInt("light_RIGHT", this.light_RIGHT);

        tag.put(Smartcitytoolkitforge1.MODID, data);
    }

    public void tick() {
        if (level != null && !level.isClientSide) {
            //System.out.println(linkedSensor.getRotationX());
            if (linkedSensor != null) {
                rotationX = linkedSensor.getRotationX();
                rotationY = linkedSensor.getRotationY();
                rotationZ = linkedSensor.getRotationZ();
                positionX = linkedSensor.getPositionX();
                positionY = linkedSensor.getPositionY();
                positionZ = linkedSensor.getPositionZ();
                light_UP = linkedSensor.getLight_UP();
                light_DOWN = linkedSensor.getLight_DOWN();
                light_FORWARD = linkedSensor.getLight_FORWARD();
                light_BACK = linkedSensor.getLight_BACK();
                light_LEFT = linkedSensor.getLight_LEFT();
                light_RIGHT = linkedSensor.getLight_RIGHT();
                update();
            }
        }
    }

    public void setRotation(float rotationX, float rotationY, float rotationZ) {
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        update();
    }

    public void setPosition(float positionX, float positionY, float positionZ) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        update();
    }

    public void setLight(int light_UP, int light_DOWN, int light_FORWARD, int light_BACK, int light_LEFT, int light_RIGHT) {
        this.light_UP = light_UP;
        this.light_DOWN = light_DOWN;
        this.light_FORWARD = light_FORWARD;
        this.light_BACK = light_BACK;
        this.light_LEFT = light_LEFT;
        this.light_RIGHT = light_RIGHT;
        update();
    }

    private void update() {
        setChanged();
        if(this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, level.registryAccess());
        return tag;
    }
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
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
        return this.rotationX;
    }

    public float getRotationY() {
        return this.rotationY;
    }

    public float getRotationZ() {
        return this.rotationZ;
    }

    public float getPositionX() {
        return this.positionX;
    }
    public float getPositionY() {
        return this.positionY;
    }
    public float getPositionZ() {
        return this.positionZ;
    }

    public void setLinkedSensor(IMUSensor sensor) {
        this.linkedSensor = sensor;
    }
    public IMUSensor getLinkedSensor() {
        return this.linkedSensor;
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