package org.example.smart.smartcitytoolkitforge1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:smartcity.db";

    public static void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                String sql = "CREATE TABLE IF NOT EXISTS sensors (" +
                        "sensor_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "device_name TEXT, " +
                        "device_id TEXT, " +
                        "created_data TEXT, " +
                        "deleted BOOLEAN, " +
                        "link TEXT);";
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}