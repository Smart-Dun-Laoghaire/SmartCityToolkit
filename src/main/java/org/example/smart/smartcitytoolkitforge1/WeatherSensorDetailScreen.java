package org.example.smart.smartcitytoolkitforge1;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.mojang.text2speech.Narrator.LOGGER;

public class WeatherSensorDetailScreen extends Screen {

    private final WeatherSensor weatherSensor;
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Smartcitytoolkitforge1.MODID, "textures/gui/weather_sensor_detail.png");
    private String weatherData = "Loading...";

    public WeatherSensorDetailScreen(WeatherSensor weatherSensor, Component title) {
        super(title);
        this.weatherSensor = weatherSensor != null ? weatherSensor : new WeatherSensor(BlockBehaviour.Properties.of(Blocks.STONE.defaultBlockState().getMaterial()), new BlockPos(0, 0, 0));
    }

    @Override
    protected void init() {
        super.init();
        // Fetch sensor data asynchronously
        fetchSensorData();
    }

    private void fetchSensorData() {
        if (weatherSensor != null && minecraft != null && minecraft.level != null) {
            // Perform asynchronous data fetch
            Minecraft.getInstance().execute(() -> {
                try {
                    weatherSensor.update(minecraft.level, weatherSensor.getLocation());
                    weatherData = weatherSensor.getCurrentWeather();
                } catch (Exception e) {
                    weatherData = "Error fetching data";
                    e.printStackTrace();
                }
            });
        } else {
            weatherData = "No sensor or Minecraft instance available";
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


        String weather = "Weather: " + weatherData;
        this.init();

        this.font.draw(poseStack, weather, x + 10, y + 70, 0xFFFFFF);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}