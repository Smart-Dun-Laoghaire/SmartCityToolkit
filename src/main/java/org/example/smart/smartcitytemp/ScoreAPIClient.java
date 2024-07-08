package org.example.smart.smartcitytemp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScoreAPIClient {

    private static final String SCORE_API_URL = "https://environmental-data-ie.spatialdynamicslab.xyz/api/v1/weather-underground/pws/observations/?station_name=IDNLAO5&start_date=2024-06-30&end_date=2024-07-01&limit=10000";
  // private static final String SCORE_API_URL = "https://api.tomorrow.io/v4/weather/forecast?location=42.3478,-71.0466&apikey=k9Uk7GkQO46QBNfSqfpcGWnKsIgxb9AF";
    public static String fetchDataFromAPI() throws IOException {
        URL url = new URL(SCORE_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}
