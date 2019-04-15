package ru.allformine.afmcp.net.http;

import ru.allformine.afmcp.AFMCorePlugin;
import sun.net.www.protocol.http.HttpURLConnection;
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
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);

            byte[] out = JSON.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.connect();

            try(OutputStream os = http.getOutputStream()) {
                os.write(out);
            }

            if(http.getResponseCode() != 200 || http.getResponseCode() != 204) {
                AFMCorePlugin.logger.error("Can't send JSON to url "+urlString+".");
                AFMCorePlugin.logger.error("JSON: "+JSON);
                AFMCorePlugin.logger.error(new BufferedReader(new InputStreamReader(http.getErrorStream())).readLine());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
