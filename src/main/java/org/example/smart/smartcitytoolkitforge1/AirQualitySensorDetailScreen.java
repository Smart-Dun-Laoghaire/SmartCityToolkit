package org.example.smart.smartcitytoolkitforge1;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AirQualitySensorDetailScreen extends Screen {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Smartcitytoolkitforge1.MODID, "textures/gui/air_quality_sensor_detail.png");
    private final AirQualitySensor airQualitySensor;

    private ScheduledExecutorService scheduler;

    protected AirQualitySensorDetailScreen(AirQualitySensor airQualitySensor, Component title) {
        super(title);
        this.airQualitySensor = airQualitySensor != null ? airQualitySensor : new AirQualitySensor(new BlockPos(0, 0, 0));
    }

    @Override
    protected void init() {
        super.init();

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                fetchSensorData();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void fetchSensorData() throws IOException, InterruptedException {
        if (airQualitySensor != null && minecraft.level != null) {
            airQualitySensor.update(minecraft.level, airQualitySensor.getLocation());

            this.minecraft.execute(() -> this.minecraft.screen = this);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        float scale = 0.3f;


        int scaledWidth = (int) (600 * scale);
        int scaledHeight = (int) (600 * scale);


        int x = (this.width - scaledWidth) / 2;
        int y = (this.height - scaledHeight) / 2;


        this.blit(poseStack, x, y, 0, 0, scaledWidth, scaledHeight);

        if (airQualitySensor != null) {

            String pm2 = "PM2.5: " + airQualitySensor.getCurrentPM2();
            String pm10 = "PM10: " + airQualitySensor.getCurrentPM10();
            String pm1 = "PM1: " + airQualitySensor.getCurrentPM1();
            String pmecO2 = "ecO2: " + airQualitySensor.getCurrentecO2();
            String pmTVOC = "TVOC: " + airQualitySensor.getCurrentPMTVOC();

           // this.init();

         //   this.font.draw(poseStack, "Air Quality Data at Dún Laoghaire:", x, y + 10, 0xFFFFFF);
            this.font.draw(poseStack, pm2 + " ug/m3", x + 10, y + 50, 0xFFFFFF);
            this.font.draw(poseStack, pm10 + " ug/m3", x + 10, y + 70, 0xFFFFFF);
            this.font.draw(poseStack, pm1 + " ug/m3", x + 10, y + 90, 0xFFFFFF);
            this.font.draw(poseStack, pmecO2 + " ppm", x + 10, y + 110, 0xFFFFFF);
            this.font.draw(poseStack, pmTVOC + " ppb", x + 10, y + 130, 0xFFFFFF);
        } else {
            this.font.draw(poseStack, "No Sensor Data Available", x + 10, y + 10, 0xFF0000);
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}