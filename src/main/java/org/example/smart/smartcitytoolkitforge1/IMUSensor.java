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
import net.minecraft.world.level.material.MapColor;
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
import java.time.LocalDateTime;

import static org.example.smart.smartcitytoolkitforge1.Smartcitytoolkitforge1.AIR_QUALITY_SENSOR_BLOCK;
import static org.example.smart.smartcitytoolkitforge1.Smartcitytoolkitforge1.IMU_BLOCK;

public class IMUSensor extends Sensor{
    // Movement-related properties

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String API_URL = "https://environmental-data-ie.spatialdynamicslab.xyz/api/v1/smart-citizen/observations";
    private static final String API_KEY = "16759";
    private static final Logger LOGGER = Logger.getLogger("MyMod");

    private float rotationX = 0;
    private float rotationY = 0;
    private float rotationZ = 0;
    private float positionX = 0;
    private float positionY = 0;
    private float positionZ = 0;

    public IMUSensor(BlockBehaviour.Properties properties, BlockPos location) {
        super(properties, location);
    }

    @Override
    public RegistryObject<Block> CreateNewBlock() {
        // Register the IMU block and return its RegistryObject
        return Smartcitytoolkitforge1.IMU_BLOCK;
    }
    @Override
    public void update(Level world, BlockPos pos) throws IOException, InterruptedException {
        // Fetch IMU data from Azure IoT service
        fetchIMUDataFromAzure();

        // Update the block entity with the new IMU data
        if (world.getBlockEntity(pos) instanceof IMUBlockEntity blockEntity) {
            blockEntity.setRotation(rotationX, rotationY, rotationZ);
            blockEntity.setPosition(positionX, positionY, positionZ);
            blockEntity.setChanged(); // Mark the block entity as dirty to save changes
        }
    }
    public void fetchIMUDataFromAzure() {
        // Simulate fetching IMU data from Azure IoT service
        // Replace this with actual Azure IoT SDK calls
        rotationX = (float) Math.random() * 360; // Simulated rotation X
        rotationY = (float) Math.random() * 360; // Simulated rotation Y
        rotationZ = (float) Math.random() * 360; // Simulated rotation Z
        positionX = (float) Math.random() * 10;  // Simulated position X
        positionY = (float) Math.random() * 10;  // Simulated position Y
        positionZ = (float) Math.random() * 10;  // Simulated position Z
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public float getPositionZ() {
        return positionZ;
    }

    /*
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VELOCITY_X, VELOCITY_Y, VELOCITY_Z);
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

        int velocityX = currentState.getValue(VELOCITY_X);
        int velocityY = currentState.getValue(VELOCITY_Y);
        int velocityZ = currentState.getValue(VELOCITY_Z);

        BlockState newState = currentState.setValue(VELOCITY_X, velocityX).setValue(VELOCITY_Y, velocityY).setValue(VELOCITY_Z, velocityZ);
        return newState;
    }


    @Override
    public List<Block> getAllBlocks() {
        return blocks;
    }

    @Override
    public void update(Level world, BlockPos pos) throws IOException, InterruptedException {
        if (!world.isClientSide()) {
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

    public int getCurrentVelocityX() {
        return currentVelocityX;
    }

    public int getCurrentVelocityY() {
        return currentVelocityY;
    }

    public int getCurrentVelocityZ() {
        return currentVelocityZ;
    }
    */
}
