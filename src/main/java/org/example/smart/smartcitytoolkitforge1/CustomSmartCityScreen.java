package org.example.smart.smartcitytoolkitforge1;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static org.example.smart.smartcitytoolkitforge1.Smartcitytoolkitforge1.AIR_QUALITY_SENSOR_BLOCK;

public class CustomSmartCityScreen extends AbstractContainerScreen<CustomSmartCityContainer> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("smartcitytoolkitforge1_19", "textures/gui/custom_smartcity_block.png");

    private final WeatherSensor weatherSensor;
    private final TemperatureSensor temperatureSensor;
    private final AirQualitySensor airQualitySensor;

    public CustomSmartCityScreen(CustomSmartCityContainer container, Inventory inv, Component title) {
        super(container, inv, Component.literal(""));

        BlockBehaviour.Properties sensorProperties = BlockBehaviour.Properties.of(Material.METAL)
                .strength(1.5f)
                .explosionResistance(6.0f)
                .sound(SoundType.METAL);

        this.weatherSensor = new WeatherSensor(sensorProperties, new BlockPos(0, 0, 0));
        this.temperatureSensor = new TemperatureSensor(new BlockPos(0, 0, 0));
        this.airQualitySensor = new AirQualitySensor(new BlockPos(0, 0, 0));
    }

    @Override
    protected void init() {
        super.init();
        int buttonWidth = 200;
        int buttonHeight = 20;
        int startX = (this.width - buttonWidth) / 2;
        int startY = (this.height - buttonHeight * 3) / 2; // Adjust Y position based on number of buttons

        this.addRenderableWidget(new Button(startX, startY, buttonWidth, buttonHeight, Component.literal("Weather Sensor"), button -> openSensorScreen("weather", weatherSensor)));
        this.addRenderableWidget(new Button(startX, startY + buttonHeight, buttonWidth, buttonHeight, Component.literal("Temperature Sensor"), button -> openSensorScreen("temperature", temperatureSensor)));
        this.addRenderableWidget(new Button(startX, startY + 2 * buttonHeight, buttonWidth, buttonHeight, Component.literal("Air Quality Sensor"), button -> openSensorScreen("air_quality", airQualitySensor)));
    }

    private void openSensorScreen(String sensorType, Sensor sensor) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {

            return;
        }

        switch (sensorType) {
            case "weather":
                minecraft.setScreen(new WeatherSensorDetailScreen((WeatherSensor) sensor, Component.literal("Weather Sensor Details")));
                break;
            case "temperature":
                minecraft.setScreen(new TemperatureSensorDetailScreen((TemperatureSensor) sensor, Component.literal("Temperature Sensor Details")));
                break;
            case "air_quality":
                minecraft.setScreen(new AirQualitySensorDetailScreen((AirQualitySensor) sensor, Component.literal("Air Quality Sensor Details")));

                // Add the Air Quality Sensor block to the player's inventory
                ItemStack itemStack = new ItemStack(AIR_QUALITY_SENSOR_BLOCK.get().asItem());
                minecraft.player.getInventory().add(itemStack);
                break;
            default:
                throw new IllegalArgumentException("Unknown sensor type: " + sensorType);
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);


        float scale = 1.4f;


        int scaledWidth = (int) (this.imageWidth * scale);
        int scaledHeight = (int) (this.imageHeight * scale);


        int x = (this.width - scaledWidth) / 2;
        int y = (this.height - scaledHeight) / 2;

        this.blit(poseStack, x, y, 0, 0, scaledWidth, scaledHeight);
    }
    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {

       // this.font.draw(poseStack, "Smart City Toolkit", 8, 6, 0xFFFFFF); // Adjust coordinates and color as needed
    }
}