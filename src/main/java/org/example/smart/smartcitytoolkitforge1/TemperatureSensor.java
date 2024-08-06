package org.example.smart.smartcitytoolkitforge1;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class TemperatureSensor extends Sensor {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String API_URL = "https://environmental-data-ie.spatialdynamicslab.xyz/api/v1/smart-citizen/observations";
    private static final String API_KEY = "16759";
    private String currentTemperature = "Loading...";

    public TemperatureSensor(BlockPos location) {
        super(BlockBehaviour.Properties.of(Material.METAL)
                        .strength(1.5f)
                        .explosionResistance(6.0f)
                        .sound(SoundType.METAL),
                location);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) throws IOException, InterruptedException {
        MinecraftServer server = event.getServer(); // Get the server instance
        if (server != null) {
            ServerLevel serverLevel = server.getLevel(Level.OVERWORLD);
            for (Sensor sensor : all) {
                sensor.update(serverLevel, sensor.getLocation());
            }
        }
    }

    @Override
    public RegistryObject<Block> CreateNewBlock() {
        return null; // Placeholder for block creation logic
    }

    @Override
    public List<Block> getAllBlocks() {
        return blocks; // Placeholder, manage blocks list as needed
    }

    @Override
    public void update(Level world, BlockPos pos) throws IOException, InterruptedException {
        if (!world.isClientSide) {
            String url = API_URL + "/?device_id=" + API_KEY + "&limit=15&offset=100";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            System.out.println("API Response: " + responseBody); // Log the raw response

            try {
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonArray resultsArray = jsonObject.getAsJsonArray("results");

                if (resultsArray != null && resultsArray.size() > 0) {
                    JsonObject firstResult = resultsArray.get(0).getAsJsonObject();
                    if (firstResult.has("value")) {
                        double temperature = firstResult.get("value").getAsDouble();
                        currentTemperature = String.valueOf(temperature);
                    } else {
                        System.out.println("Temperature 'value' not found in JSON response.");
                    }
                } else {
                    System.out.println("No results found in JSON response.");
                }
            } catch (Exception e) {
                System.err.println("Error parsing JSON response: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public String getCurrentTemperature() {
        return currentTemperature;
    }
}