package org.example.smart.smartcitytemp;

import java.io.IOException;

public class TemperatureAPI {
    private static int currentTemperature = 25;  // Default value for demonstration
    private static ScoreAPIClient WeatherAPIClient;

    public static int getCurrentTemperature() {
        return currentTemperature;
    }

    public static void updateTemperature() {
        try {
            String apiResponse = WeatherAPIClient.fetchDataFromAPI();
            WeatherData weatherData = WeatherDataParser.parseWeatherData(apiResponse);
            currentTemperature = (int) weatherData.getTemperature();
        } catch (IOException e) {
            // Handle error gracefully
        }
    }
}
