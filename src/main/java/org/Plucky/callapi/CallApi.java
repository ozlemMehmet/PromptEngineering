package org.Plucky.callapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class CallApi {
    public static String getApiMessage() {
        String urlString = "https://api.sampleapis.com/wines/reds"; // Replace with your API URL

        try {
            URL url = URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return "Response: " + response.toString();
            } else {
                return "GET request failed";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlString + " not found";
    }
}
