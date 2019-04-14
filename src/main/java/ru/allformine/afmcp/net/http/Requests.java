package ru.allformine.afmcp.net.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import ru.allformine.afmcp.AFMCorePlugin;

public class Requests {
    public static void sendPostJSON(String JSON, String url) {
        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(JSON, "UTF-8");
            request.addHeader("Content-Type", "application/json");
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 204 || response.getStatusLine().getStatusCode() != 200) {
                AFMCorePlugin.logger.error("Error sending JSON data.");
                AFMCorePlugin.logger.error("JSON: "+JSON);
                AFMCorePlugin.logger.error("Response: "+response.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
