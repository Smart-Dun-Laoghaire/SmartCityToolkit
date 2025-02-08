package org.example.smart.smartcitytoolkitforge1;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import com.google.gson.*;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Comparator;

public class Azure {
    private final IMUData Data;
    private static final String CONNECTION_STRING = "";
    private static final String CONTAINER_NAME = "imu-data";

    public Azure() {
        Data = new IMUData();
    }

    long lastUpdateTime = 0;
    long updateInterval = 1000;  // 1 seconds

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= updateInterval) {
            fetchData();  // Update IMU data
            lastUpdateTime = currentTime;  // Update the last update time
        }
    }

    public void simulateData() {
        Data.positionY += 0.0f;
        Data.positionX += 0f;
        Data.positionZ += 0f;

        Data.rotationX += 0.1f;
        Data.rotationY += 0.0f;
        Data.rotationZ += 0.1f;

        Data.light_UP = 135;
        Data.light_DOWN = 55;
        Data.light_FORWARD = 75;
        Data.light_BACK = 255;
        Data.light_LEFT = 98;
        Data.light_RIGHT = 33;

        if (Data.positionX >= 10) {
            resetSimulatedData();
        }
    }

    public void resetSimulatedData() {
        Data.positionY = 0f;
        Data.positionX = 0f;
        Data.positionZ = 0f;

        Data.rotationX = 0f;
        Data.rotationY = 0f;
        Data.rotationZ = 0f;

        Data.light_UP = 255;
        Data.light_DOWN = 255;
        Data.light_FORWARD = 255;
        Data.light_BACK = 255;
        Data.light_LEFT = 255;
        Data.light_RIGHT = 255;
    }

    public void fetchData() {
        String json = fetchLatestBlobData();
        if (json == null || json.isEmpty()) {
            System.out.println("No data received.");
            return;
        }

        // Split the content by newline (assuming each JSON entry is separated by a newline)
        String[] messages = json.split("\n");

        if (messages.length > 0) {
            // Assuming the latest entry is the last one (or adjust based on your logic)
            String latestMessage = messages[messages.length - 1];

            // Parse the latest JSON message
            try {
                JsonObject jsonObject = JsonParser.parseString(latestMessage).getAsJsonObject();

                // Extract and decode the base64-encoded "Body"
                if (jsonObject.has("Body") && !jsonObject.get("Body").isJsonNull()) {
                    String base64Body = jsonObject.get("Body").getAsString();
                    byte[] decodedBytes = Base64.getDecoder().decode(base64Body);
                    String decodedJson = new String(decodedBytes);

                    System.out.println("Decoded IMU Data: " + decodedJson);

                    // Deserialize into IMUData
                    Gson gson = new GsonBuilder().registerTypeAdapter(IMUData.class, new IMUDataDeserializer()).create();
                    IMUData imuData = gson.fromJson(decodedJson, IMUData.class);

                    // Update IMUData object
                    this.Data.rotationX = imuData.rotationX;
                    this.Data.rotationY = imuData.rotationY;
                    this.Data.rotationZ = imuData.rotationZ;
                    this.Data.light_UP = imuData.light_UP;
                    this.Data.light_DOWN = imuData.light_DOWN;
                    this.Data.light_FORWARD = imuData.light_FORWARD;
                    this.Data.light_BACK = imuData.light_BACK;
                    this.Data.light_LEFT = imuData.light_LEFT;
                    this.Data.light_RIGHT = imuData.light_RIGHT;
                } else {
                    System.out.println("No 'Body' field found in JSON.");
                }
            } catch (JsonSyntaxException e) {
                System.out.println("Error parsing JSON: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No JSON messages found in the blob.");
        }
    }
    public static String fetchLatestBlobData() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(CONNECTION_STRING)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);

        // Find the most recently modified blob
        BlobItem latestBlob = containerClient.listBlobs()
                .stream()
                .max(Comparator.comparing(blob -> blob.getProperties().getLastModified()))
                .orElse(null);

        if (latestBlob == null) {
            System.out.println("No blobs found.");
            return null;
        }

        // Fetch blob content
        BlobClient blobClient = containerClient.getBlobClient(latestBlob.getName());
        String jsonData = new String(blobClient.downloadContent().toBytes(), StandardCharsets.UTF_8);

        //System.out.println("Latest IMU Data: " + jsonData);
        return jsonData;
    }

    private void updateData(IMUData newData) {
        Data.rotationX = newData.rotationX;
        Data.rotationY = newData.rotationY;
        Data.rotationZ = newData.rotationZ;

        Data.light_UP = newData.light_UP;
        Data.light_DOWN = newData.light_DOWN;
        Data.light_FORWARD = newData.light_FORWARD;
        Data.light_BACK = newData.light_BACK;
        Data.light_LEFT = newData.light_LEFT;
        Data.light_RIGHT = newData.light_RIGHT;
    }

    public IMUData getData() {
        return Data;
    }

    // Custom Deserializer to Flatten JSON
    private static class IMUDataDeserializer implements JsonDeserializer<IMUData> {
        @Override
        public IMUData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            IMUData imuData = new IMUData();

            // Extract orientation values
            JsonObject orientation = jsonObject.getAsJsonObject("Orientation");
            imuData.rotationX = orientation.get("X").getAsFloat();
            imuData.rotationY = orientation.get("Y").getAsFloat();
            imuData.rotationZ = orientation.get("Z").getAsFloat();

            // Extract light values
            JsonObject light = jsonObject.getAsJsonObject("Light");
            imuData.light_UP = light.get("UP").getAsInt();
            imuData.light_DOWN = light.get("DOWN").getAsInt();
            imuData.light_FORWARD = light.get("FORWARD").getAsInt();
            imuData.light_BACK = light.get("BACK").getAsInt();
            imuData.light_LEFT = light.get("LEFT").getAsInt();
            imuData.light_RIGHT = light.get("RIGHT").getAsInt();

            return imuData;
        }
    }
}
