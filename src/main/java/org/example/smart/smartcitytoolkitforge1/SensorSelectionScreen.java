package org.example.smart.smartcitytoolkitforge1;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class SensorSelectionScreen extends Screen {
    private final BlockPos blockPos;
    private final Level world;

    protected SensorSelectionScreen(BlockPos blockPos, Level world) {
        super(Component.literal("Select Sensor"));

        if (blockPos == null || world == null) {
            throw new IllegalArgumentException("BlockPos and Level cannot be null");
        }

        this.blockPos = blockPos;
        this.world = world;
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 150;
        int buttonHeight = 20;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 6;

        // Add buttons for each sensor type
        addButton(x, y, buttonWidth, buttonHeight, "Temperature Sensor", "Temperature");
        y += 30;
        addButton(x, y, buttonWidth, buttonHeight, "Air Quality Sensor", "Air Quality");
        y += 30;
        addButton(x, y, buttonWidth, buttonHeight, "Weather Sensor", "Weather");
    }

    private void addButton(int x, int y, int buttonWidth, int buttonHeight, String buttonText, String sensorType) {
        Button button = new Button(x, y, buttonWidth, buttonHeight, Component.literal(buttonText), (btn) -> {
            try {
                if (sensorType == null || blockPos == null || world == null) {
                    throw new IllegalArgumentException("Invalid parameters for block creation.");
                }
                createSmartCityBlock(sensorType);
            } catch (Exception e) {
                e.printStackTrace();
                Minecraft.getInstance().player.displayClientMessage(Component.literal("An error occurred: " + e.getMessage()), true);
            }
        });
        this.addRenderableWidget(button);
    }

    private void createSmartCityBlock(String sensorType) {
        try {
            if (blockPos == null || world == null || sensorType == null) {
                throw new IllegalArgumentException("BlockPos, Level, and sensorType cannot be null");
            }
            SmartCityBlock.createSmartCityBlock(world, blockPos, sensorType);
            Minecraft.getInstance().setScreen(null); // Close the screen after the block is created
        } catch (Exception e) {
            e.printStackTrace();
            Minecraft.getInstance().player.displayClientMessage(Component.literal("An error occurred: " + e.getMessage()), true);
        }

    }
}