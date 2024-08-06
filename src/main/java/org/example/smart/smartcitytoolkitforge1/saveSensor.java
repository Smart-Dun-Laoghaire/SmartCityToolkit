package org.example.smart.smartcitytoolkitforge1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class saveSensor {
    private static final String DB_URL = "jdbc:sqlite:smartcity.db";

    public void saveSensor(Sensor sensor) {
        String sql = "INSERT INTO sensors(sensor_type, device_name, device_id, created_data, deleted, link, x, y, z) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sensor.getClass().getSimpleName());
            pstmt.setString(2, sensor.getDeviceName());
            pstmt.setString(3, sensor.getDeviceId());
            pstmt.setString(4, sensor.getCreatedTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setBoolean(5, sensor.isDeleted());
            pstmt.setString(6, sensor.getLink());
            pstmt.setInt(7, sensor.getLocation().getX());
            pstmt.setInt(8, sensor.getLocation().getY());
            pstmt.setInt(9, sensor.getLocation().getZ());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}