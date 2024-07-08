package org.example.smart.smartcitytemp;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import java.io.IOException;

public class TemperatureSensorBlock extends Block {
    public TemperatureSensorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        if (!world.isClientSide) {
            try {
                String weatherData = WeatherAPIClient.fetchWeatherData();
                // Parse the temperature from the weather data
                String temperature = parseTemperature(weatherData);
                player.displayClientMessage(Component.literal("Temperature: " + temperature), true);
            } catch (IOException e) {
                player.displayClientMessage(Component.literal("Error fetching temperature data"), true);
            }
        }
    }

    private String parseTemperature(String weatherData) {
        // Implement your parsing logic here
        return "25°C"; // Example placeholder
    }
}