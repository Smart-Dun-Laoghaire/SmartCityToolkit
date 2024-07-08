package org.example.smart.smartcitytemp;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@Mod(TemperatureSensorBlockEntity.MODID)
public class TemperatureSensorBlockEntity {

    public static final String MODID = "smartcitytemp";
    private static final Logger LOGGER = LogUtils.getLogger();
    private Timer timer;

    public TemperatureSensorBlockEntity() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((FMLCommonSetupEvent event) -> setup());

        // Register events for this class
        MinecraftForge.EVENT_BUS.register(this);

        // Register mod config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Schedule a timer to periodically update Minecraft weather
        timer = new Timer();
        timer.schedule(new WeatherUpdateTask(), 0, 600000); // Update weather every 10 minutes
    }

    private void setup() {
        LOGGER.info("HELLO FROM SETUP");
        // Additional setup code, if any
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // Check for client ticks to update client-side weather if needed
        if (event.phase == TickEvent.Phase.END) {
            // Perform client-side logic here
        }
    }

    private class WeatherUpdateTask extends TimerTask {
        @Override
        public void run() {
            try {
                String weatherData = ScoreAPIClient.fetchDataFromAPI();
                // Parse weatherData and update Minecraft weather accordingly
                updateMinecraftWeather(weatherData);
            } catch (IOException e) {
                LOGGER.error("Failed to fetch weather data from ScoreAPI: {}", e.getMessage());
            }
        }
    }

    private void updateMinecraftWeather(String weatherData) {
        // Implement logic to parse weatherData and update Minecraft weather
        // Example logic:
        // if (weatherData.contains("rain")) {
        //     Minecraft.getInstance().world.setRainStrength(1.0f);
        // } else {
        //     Minecraft.getInstance().world.setRainStrength(0.0f);
        // }
    }
}