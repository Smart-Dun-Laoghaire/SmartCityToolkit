package org.example.smart.smartcitytoolkitforge1;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class CustomSmartCityBlock extends Block {

    private final Sensor linkedSensor;

    public CustomSmartCityBlock(Properties properties, Sensor sensor) {
        super(properties);
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor cannot be null");
        }
        if (!(sensor instanceof TemperatureSensor) && !(sensor instanceof AirQualitySensor)) {
            throw new IllegalArgumentException("Unsupported sensor type for block");
        }
        this.linkedSensor = sensor;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {

            return InteractionResult.SUCCESS;
        }


        if (player instanceof ServerPlayer serverPlayer) {
         //   player.displayClientMessage(Component.literal("Block clicked"), true);

            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                    (id, inventory, buf) -> new CustomSmartCityContainer(id, inventory, pos),
                    Component.translatable("container.smartcitytoolkitforge1.custom_smartcity_container")
            ), pos);
        } else {
            System.err.println("Player is not an instance of ServerPlayer!");
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CustomSmartCityBlockEntity) {
                ((CustomSmartCityBlockEntity) blockEntity).clearContent();
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }
}