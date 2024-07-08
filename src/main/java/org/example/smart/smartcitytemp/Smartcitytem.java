package org.example.smart.smartcitytemp;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;
import net.minecraft.world.item.CreativeModeTabs;

import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;





import java.io.IOException;

@Mod(Smartcitytem.MODID)
public class Smartcitytem {

    private TemperatureAPI temperatureAPI;
    public static final String MODID = "smartcitytemp";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Smartcitytem() {
        this.temperatureAPI = new TemperatureAPI();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(WeatherOverlayRenderer.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        try {
            String apiResponse = ScoreAPIClient.fetchDataFromAPI();
            WeatherData weatherData = WeatherDataParser.parseWeatherData(apiResponse);

            if (weatherData.getTemperature() > 25) {
                setSunnyWeather();
            } else {
                setRainyWeather();
            }

            LOGGER.info("Fetched Temperature: {}", weatherData.getTemperature());
        } catch (IOException e) {
            LOGGER.error("Error fetching weather data from API: {}", e.getMessage());
        }
    }

    private void setSunnyWeather() {
        Minecraft.getInstance().level.setRainLevel(0);
        Minecraft.getInstance().level.setThunderLevel(0);
    }

    private void setRainyWeather() {
        Minecraft.getInstance().level.setRainLevel(1);
        Minecraft.getInstance().level.setThunderLevel(0.2F);
    }

    @SubscribeEvent
    public void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == CreativeModeTabs.getDefaultTab()) {
            event.accept(ModItems.TEMPERATURE_SENSOR.get());
            event.accept(ModItems.AIR_QUALITY_SENSOR.get());
        }
    }
}