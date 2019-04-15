package ru.allformine.afmcp.net.http;

import ru.allformine.afmcp.AFMCorePlugin;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class Requests {
    public static void sendPostJSON(String JSON, String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            HttpsURLConnection connection = (HttpsURLConnection)con;
            connection.setRequestMethod("POST"); // PUT is another valid option
            connection.setDoOutput(true);

            byte[] out = JSON.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            connection.setFixedLengthStreamingMode(length);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.connect();

            try(OutputStream os = connection.getOutputStream()) {
                os.write(out);
            }

            if(connection.getResponseCode() != 200 || connection.getResponseCode() != 204) {
                AFMCorePlugin.logger.error("Can't send JSON to url "+urlString+".");
                AFMCorePlugin.logger.error("JSON: "+JSON);
                AFMCorePlugin.logger.error(new BufferedReader(new InputStreamReader(connection.getErrorStream())).readLine());
            }
        } catch(Exception e) {
            AFMCorePlugin.logger.error("Can't send JSON to url "+urlString+".");
            AFMCorePlugin.logger.error("JSON: "+JSON);

            e.printStackTrace();
        }
    }
}
