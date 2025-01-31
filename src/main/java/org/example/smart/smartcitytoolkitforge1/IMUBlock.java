package org.example.smart.smartcitytoolkitforge1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IMUBlock extends Block implements EntityBlock {
    private final Sensor linkedSensor;
    public IMUBlock(Properties properties, Sensor sensor) {
        super(properties);
        if (sensor == null) {
            //throw new IllegalArgumentException("Sensor cannot be null");
        }
        if (!(sensor instanceof IMUSensor)) {
            throw new IllegalArgumentException("Unsupported sensor type for block");
        }
        this.linkedSensor = sensor;
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IMUBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof IMUBlockEntity) {
                ((IMUBlockEntity) blockEntity).tick();
            }
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (level.getBlockEntity(pos) instanceof IMUBlockEntity blockEntity) {
                blockEntity.animate(); // Handle client-side animation
            }
        });
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level world, BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CustomSmartCityBlockEntity) {
                ((CustomSmartCityBlockEntity) blockEntity).clearContent();
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }


}