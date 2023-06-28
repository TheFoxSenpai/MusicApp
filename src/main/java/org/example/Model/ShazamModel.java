package org.example.Model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Properties;

public class ShazamModel {

  

    public String recognizeSong(String songPath) throws IOException, InterruptedException {
        byte[] songData = Files.readAllBytes(Path.of(songPath));
        String base64Data = Base64.getEncoder().encodeToString(songData);

        HttpClient client = HttpClient.newHttpClient();
        Properties properties = new Properties();      properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        String API_KEY = properties.getProperty("API_KEY");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://shazam.p.rapidapi.com/songs/detect"))
                .header("x-rapidapi-key", API_KEY)
                .header("x-rapidapi-host", "shazam.p.rapidapi.com")
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(base64Data))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        String responseBody = response.body();
        HttpHeaders headers = response.headers();

        System.out.println("Status code: " + statusCode);
        System.out.println("Response body: " + responseBody);
        System.out.println("Headers: " + headers);

        if (statusCode == 200) {
            return responseBody;
        } else if (statusCode == 204) {
            throw new RuntimeException("The song could not be recognized.");
        } else {
            throw new RuntimeException("Failed to recognize song. Status code: " + statusCode);
        }
    }
}
