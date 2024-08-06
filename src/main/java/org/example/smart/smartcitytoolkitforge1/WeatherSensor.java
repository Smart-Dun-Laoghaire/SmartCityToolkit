package org.example.smart.smartcitytoolkitforge1;

import static com.mojang.text2speech.Narrator.LOGGER;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WeatherSensor extends Sensor {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "db1a188a4c32408efcaf6ba4d2a4b458"; // Replace with your API key

    private String currentWeather = "broken clouds";
    private int ticksSinceLastUpdate = 0;
    private static final int UPDATE_INTERVAL = 1200; // Update interval in ticks

    public WeatherSensor(BlockBehaviour.Properties properties, BlockPos location) {
        super(properties, location);
        Sensor.all.add(this);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = event.getServer(); // Get the server instance
        if (server != null) {
            ServerLevel serverLevel = server.getLevel(Level.OVERWORLD);
            for (Sensor sensor : all) {
                if (sensor instanceof WeatherSensor) {
                    WeatherSensor weatherSensor = (WeatherSensor) sensor;
                    if (weatherSensor.ticksSinceLastUpdate >= UPDATE_INTERVAL) {
                        weatherSensor.ticksSinceLastUpdate = 0;
                        weatherSensor.update(serverLevel, sensor.getLocation());
                    } else {
                        weatherSensor.ticksSinceLastUpdate++;
                    }
                }
            }
        }
    }

    @Override
    public RegistryObject<Block> CreateNewBlock() {
        // Implement how a new block is created, return RegistryObject<Block>
        return null;
    }

    @Override
    public List<Block> getAllBlocks() {
        // Return the list of blocks associated with the sensor
        return blocks;
    }

    @Override
    public void update(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            CompletableFuture.runAsync(() -> {
                try {
                    String city = "Dun%20Laoghaire";
                    String url = API_URL + "?q=" + city + "&APPID=" + API_KEY;

                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


                    if (response.statusCode() == 200) {
                        String responseBody = response.body();

                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        JsonArray weatherArray = jsonObject.getAsJsonArray("weather");

                        if (weatherArray.size() > 0) {
                            JsonObject weather = weatherArray.get(0).getAsJsonObject();
                            if (weather.has("description")) {
                                currentWeather = weather.get("description").getAsString();
                            } else {
                                currentWeather = "Unknown";
                                LOGGER.warn("Weather description not found in API response.");
                            }
                        } else {
                            currentWeather = "Unknown";
                            LOGGER.warn("Weather array is empty.");
                        }

                        // Update particle effects based on weather
                        world.getServer().execute(() -> {
                            switch (currentWeather) {
                                case "drizzle":
                                    world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
                                    break;
                                case "rain":
                                    world.addParticle(ParticleTypes.RAIN, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
                                    break;
                                case "snow":
                                    world.addParticle(ParticleTypes.SNOWFLAKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
                                    break;
                                case "clear":
                                case "clouds":
                                default:
                                    world.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
                                    break;
                            }
                        });
                    } else {
                        LOGGER.error("API request failed with status code: {}", response.statusCode());
                        currentWeather = "Unknown";
                    }
                } catch (IOException | InterruptedException e) {
                    LOGGER.error("Error fetching or processing weather data", e);
                }
            });
        }
    }

    public String getCurrentWeather() {
        return currentWeather;
    }
}