package org.example.smart.smartcitytemp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;

public class WeatherDataParser {

    public static WeatherData parseWeatherData(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        // Example parsing for temperature
        double temperature = jsonObject.getAsJsonObject("observations").getAsJsonObject("temp").getAsDouble();

        // Parse other weather attributes as needed

        return new WeatherData(temperature);
    }
}
