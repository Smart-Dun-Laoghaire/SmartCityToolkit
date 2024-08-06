package org.example.smart.smartcitytoolkitforge1;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SensorDataAccessObject {
    private static final String URL = "jdbc:sqlite:/Users/nikitakamat/Desktop/newsmartcitytoolkit/database/SmartCityToolkitDB.db";
   // Data Source=c:\mydb.db;Version=3;

    public List<Sensor> getSensorsSortedById() {
        List<Sensor> sensors = new ArrayList<>();
        String sql = "SELECT * FROM sensors ORDER BY sensor_id";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String sensorType = rs.getString("sensor_type");
                int sensorId = rs.getInt("sensor_id");
                String deviceName = rs.getString("device_name");
                String deviceId = rs.getString("device_id");
                String createdData = rs.getString("created_data");
                boolean deleted = rs.getBoolean("deleted");
                String link = rs.getString("link");

                LocalDateTime createdTimestamp = LocalDateTime.parse(createdData, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                BlockPos location = new BlockPos(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                Sensor sensor = null;

                switch (sensorType) {
                    case "TemperatureSensor":
                        sensor = new TemperatureSensor(location);
                        break;
                    case "WeatherSensor":
                        sensor = new WeatherSensor(BlockBehaviour.Properties.of(Material.METAL), location);
                        break;
                    case "AirQualitySensor":
                        sensor = new AirQualitySensor(location);
                        break;
                }

                if (sensor != null) {
                    sensor.setDeviceId(deviceId);
                    sensor.setCreatedTimestamp(createdTimestamp);
                    sensor.setDeleted(deleted);
                    sensor.setLink(link);
                    sensors.add(sensor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sensors;
    }
}