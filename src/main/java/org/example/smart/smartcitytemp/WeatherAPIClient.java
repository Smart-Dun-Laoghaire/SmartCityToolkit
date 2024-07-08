package org.example.smart.smartcitytemp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class WeatherAPIClient {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://api.tomorrow.io/v4/weather/forecast?location=42.3478,-71.0466&apikey=k9Uk7GkQO46QBNfSqfpcGWnKsIgxb9AF";

    public static String fetchWeatherData() throws IOException {
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }
}