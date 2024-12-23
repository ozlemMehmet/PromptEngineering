package org.Plucky.callapi;

import org.Plucky.model.Wine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CallApi {
    private static final String API_URL = "https://api.sampleapis.com/wines/reds";

    public static class WineApiException extends Exception {
        public WineApiException(String message) {
            super(message);
        }

        public WineApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static List<Wine> getWines() throws WineApiException {
        List<Wine> wines = new ArrayList<>();
        HttpURLConnection connection = null;
        BufferedReader in = null;

        try {
            URL url = URI.create(API_URL).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new WineApiException("API request failed with response code: " + responseCode);
            }

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            try {
                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject wineJson = jsonArray.getJSONObject(i);
                    try {
                        Wine wine = new Wine(
                            wineJson.getString("winery"),
                            wineJson.getString("wine"),
                            wineJson.getString("rating"),
                            wineJson.getInt("reviews"),
                            wineJson.getString("location"),
                            wineJson.getString("image")
                        );
                        wines.add(wine);
                    } catch (JSONException e) {
                        System.err.println("Error parsing wine object at index " + i + ": " + e.getMessage());
                        // Continue processing other wines even if one fails
                    }
                }
            } catch (JSONException e) {
                throw new WineApiException("Failed to parse JSON response", e);
            }

            return wines;

        } catch (IOException e) {
            throw new WineApiException("Failed to connect to wine API", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.println("Error closing reader: " + e.getMessage());
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

