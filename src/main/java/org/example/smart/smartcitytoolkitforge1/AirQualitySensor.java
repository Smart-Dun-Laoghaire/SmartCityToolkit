package org.example.smart.smartcitytoolkitforge1;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Logger;

import static org.example.smart.smartcitytoolkitforge1.Smartcitytoolkitforge1.AIR_QUALITY_SENSOR_BLOCK;

public class AirQualitySensor extends Sensor {
    public static final IntegerProperty PM2_PROPERTY = IntegerProperty.create("pm2", 0, 100); // Example range 0-100
    public static final IntegerProperty PM10_PROPERTY = IntegerProperty.create("pm10", 0, 100); // Example range 0-100
    public static final IntegerProperty PM1_PROPERTY = IntegerProperty.create("pm1", 0, 100); // Example range 0-100
    public static final IntegerProperty PMTVOC_PROPERTY = IntegerProperty.create("pmTVOC", 0, 5500); // Example range 0-100
    public static final IntegerProperty PMeCO2_PROPERTY = IntegerProperty.create("pmecO2", 0, 2000); // Example range 0-100

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String API_URL = "https://environmental-data-ie.spatialdynamicslab.xyz/api/v1/smart-citizen/observations";
    private static final String API_KEY = "16759";
    private static final Logger LOGGER = Logger.getLogger("MyMod");

    private int currentPM2 = 0;
    private int currentPM10 = 0;
    private int currentPM1 = 0;
    private int currentecO2 = 0;
    private int currentTVOC = 0;


    public AirQualitySensor(BlockPos location) {
        super(BlockBehaviour.Properties.of(Material.METAL)
                        .strength(1.5f)
                        .explosionResistance(6.0f)
                        .sound(SoundType.METAL),
                location);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PM2_PROPERTY, PM10_PROPERTY, PM1_PROPERTY, PMTVOC_PROPERTY, PMeCO2_PROPERTY);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) throws IOException, InterruptedException {
        MinecraftServer server = event.getServer();
        if (server != null) {
            ServerLevel serverLevel = server.getLevel(Level.OVERWORLD);
            if (serverLevel != null) {
                for (Sensor sensor : all) {
                    sensor.update(serverLevel, sensor.getLocation());
                }
            }
        }
    }

    public BlockState createNewBlock(Level world, BlockPos pos) {

        BlockState currentState = world.getBlockState(pos);

        int pm2 = currentState.getValue(PM2_PROPERTY);
        int pm10 = currentState.getValue(PM10_PROPERTY);
        int pm1 = currentState.getValue(PM1_PROPERTY);
        int pmecO2 = currentState.getValue(PMeCO2_PROPERTY);
        int pmTVOC = currentState.getValue(PMTVOC_PROPERTY);


        BlockState newState = currentState.setValue(PM2_PROPERTY, pm2).setValue(PM10_PROPERTY, pm10).setValue(PM1_PROPERTY, pm1).setValue(PMeCO2_PROPERTY, pmecO2).setValue(PMTVOC_PROPERTY, pmTVOC);
        return newState;
    }

    @Override
    public RegistryObject<Block> CreateNewBlock() {

        return AIR_QUALITY_SENSOR_BLOCK;
    }

    @Override
    public List<Block> getAllBlocks() {
        return blocks;
    }

    @Override
    public void update(Level world, BlockPos pos) throws IOException, InterruptedException {
        if (!world.isClientSide) {
            String url = API_URL + "/?device_id=" + API_KEY + "&limit=15&offset=100";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();


            System.out.println("API Response: " + responseBody);

            try {

                JsonReader reader = new JsonReader(new StringReader(responseBody));
                reader.setLenient(true);
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                // Process JSON data
                if (jsonObject.has("results") && jsonObject.get("results").isJsonArray()) {
                    JsonArray resultsArray = jsonObject.getAsJsonArray("results");
                    if (!resultsArray.isEmpty()) {
                        JsonObject firstResult = resultsArray.get(0).getAsJsonObject();
                        if (firstResult.has("PM 2.5")) {
                            this.currentPM2 = firstResult.get("PM 2.5").getAsInt();
                        }
                        if (firstResult.has("PM 10")) {
                            this.currentPM10 = firstResult.get("PM 10").getAsInt();
                        }
                        if (firstResult.has("PM 1")) {
                            this.currentPM1 = firstResult.get("PM 1").getAsInt();
                        }
                        if (firstResult.has("eCO2")) {
                            this.currentecO2 = firstResult.get("ecO2").getAsInt();
                        }
                        if (firstResult.has("TVOC")) {
                            this.currentTVOC = firstResult.get("TVOC").getAsInt();
                        }
                    }
                }


            } catch (JsonSyntaxException e) {
                System.err.println("Failed to parse JSON: " + e.getMessage());
                System.err.println("Response Body: " + responseBody);
            }

            ServerLevel serverLevel = (ServerLevel) world;
            BlockState currentState = world.getBlockState(pos);
            BlockState newState = currentState.setValue(PM2_PROPERTY, this.currentPM2).setValue(PM10_PROPERTY, this.currentPM10);
            serverLevel.setBlock(pos, newState, 3);
        }
    }

    public int getCurrentPM2() {
        return currentPM2;
    }

    public int getCurrentPM10() {
        return currentPM10;
    }

    public int getCurrentPM1() {
        return currentPM1;
    }

    public int getCurrentPMTVOC() {
        return currentTVOC;
    }

    public int getCurrentecO2() {
        return currentecO2;
    }


    private void showParticles(ServerLevel serverWorld, Player player, int pm2, int pm10, int pm1, int ecO2, int TVOC) {
        if (serverWorld == null || player == null) {
            return;
        }

        BlockPos playerPos = player.blockPosition();


        int numParticles = 20;
        double spread = 1.0;

        // Define particle effects based on air quality values
        if (pm2 > 50) {
            for (int i = 0; i < numParticles; i++) {
                double xOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                double yOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                double zOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                serverWorld.sendParticles(ParticleTypes.LARGE_SMOKE,
                        playerPos.getX() + 0.5 + xOffset, playerPos.getY() + 1.0 + yOffset, playerPos.getZ() + 0.5 + zOffset,
                        1, 0.0, 0.0, 0.0, 0.1);
            }
        }
        if (pm10 > 50) {
            for (int i = 0; i < numParticles; i++) {
                double xOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                double yOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                double zOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                serverWorld.sendParticles(ParticleTypes.SMOKE,
                        playerPos.getX() + 0.5 + xOffset, playerPos.getY() + 1.0 + yOffset, playerPos.getZ() + 0.5 + zOffset,
                        1, 0.0, 0.0, 0.0, 0.1);
            }
        }
        if (pm1 > 50) {
            for (int i = 0; i < numParticles; i++) {
                double xOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                double yOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                double zOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                serverWorld.sendParticles(ParticleTypes.SMOKE,
                        playerPos.getX() + 0.5 + xOffset, playerPos.getY() + 1.0 + yOffset, playerPos.getZ() + 0.5 + zOffset,
                        1, 0.0, 0.0, 0.0, 0.1);
            }
        }
        if (ecO2 < 400 || ecO2 > 1000) {
            for (int i = 0; i < numParticles; i++) {
                double xOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                double yOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                double zOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                serverWorld.sendParticles(ParticleTypes.SMOKE,
                        playerPos.getX() + 0.5 + xOffset, playerPos.getY() + 1.0 + yOffset, playerPos.getZ() + 0.5 + zOffset,
                        1, 0.0, 0.0, 0.0, 0.1);
            }
        }
        if (TVOC < 0 || TVOC > 400) {
            for (int i = 0; i < numParticles; i++) {
                double xOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                double yOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                double zOffset = (serverWorld.random.nextDouble() - 0.5) * spread;
                serverWorld.sendParticles(ParticleTypes.SMOKE,
                        playerPos.getX() + 0.5 + xOffset, playerPos.getY() + 1.0 + yOffset, playerPos.getZ() + 0.5 + zOffset,
                        1, 0.0, 0.0, 0.0, 0.1);
            }
        }
    }
}