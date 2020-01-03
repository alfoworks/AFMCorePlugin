package ru.alfomine.afmcp.webhookapi;

import ru.alfomine.afmcp.AFMCorePlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class Request {
    public static void sendNewPost(String data, String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            HttpsURLConnection connection = (HttpsURLConnection) con;
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            connection.setFixedLengthStreamingMode(length);
            connection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.addRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.11) ");
            connection.connect();

            try (OutputStream os = connection.getOutputStream()) {
                os.write(out);
            }

            if (connection.getErrorStream() != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append("\n").append(line);
                }

                AFMCorePlugin.log("Error sending POST with:", Level.SEVERE);
                AFMCorePlugin.log("Data: " + data, Level.SEVERE);
                AFMCorePlugin.log("Response: " + result, Level.SEVERE);
                AFMCorePlugin.log("Status code: " + connection.getResponseCode(), Level.SEVERE);
            }
        } catch (Exception e) {
            AFMCorePlugin.log("Error sending POST with:", Level.SEVERE);
            AFMCorePlugin.log("Data: " + data, Level.SEVERE);
            AFMCorePlugin.log("No response.", Level.SEVERE);

            e.printStackTrace();
        }
    }
}
