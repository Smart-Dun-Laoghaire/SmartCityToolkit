package org.example.smart.smartcitytoolkitforge1;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class TemperatureSensorDetailScreen extends Screen {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Smartcitytoolkitforge1.MODID, "textures/gui/temperature_sensor_detail.png");
    private final TemperatureSensor temperatureSensor;
    private String temperatureDisplay = "Fetching data..."; // Display string for temperature

    protected TemperatureSensorDetailScreen(TemperatureSensor temperatureSensor, Component title) {
        super(title);
        this.temperatureSensor = temperatureSensor != null ? temperatureSensor : new TemperatureSensor(new BlockPos(0, 0, 0));
    }



    private void fetchSensorData() {
        if (temperatureSensor != null) {
            Minecraft.getInstance().execute(() -> {
                if (Minecraft.getInstance().level != null) { // Check if minecraft.level is not null
                    try {
                        temperatureSensor.update(Minecraft.getInstance().level, temperatureSensor.getLocation());
                        temperatureDisplay = "Temperature: " + temperatureSensor.getCurrentTemperature();
                        //this.init();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        temperatureDisplay = "Error fetching data";
                    }
                } else {
                    temperatureDisplay = "No active world session";
                }
            });
        }
    }

    @Override
    protected void init() {
        super.init();
        fetchSensorData();
        sendClickableMessage();
    }

    private void sendClickableMessage() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            Component message = Component.literal("Click on the link for more info!")
                    .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://environmental-data-ie.spatialdynamicslab.xyz/api/v1/smart-citizen/observations/?device_id=16759")));

            minecraft.player.sendSystemMessage(message);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        float scale = 0.3f;

        // Calculate the new dimensions based on the scale factor
        int scaledWidth = (int) (600 * scale);
        int scaledHeight = (int) (600 * scale);

        // Calculate the position to center the scaled image
        int x = (this.width - scaledWidth) / 2;
        int y = (this.height - scaledHeight) / 2;

        // Render the texture with the new dimensions
        this.blit(poseStack, x, y, 0, 0, scaledWidth, scaledHeight);


        // Debugging: Print the temperatureDisplay string
      //  this.font.draw(poseStack, "Temperature Data at Dún Laoghaire:", x , y + 10, 0xFFFFFF);
        this.font.draw(poseStack, "Temperature: " + temperatureSensor.getCurrentTemperature() + "°C", x + 10, y + 70, 0xFFFFFF);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}