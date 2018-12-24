package ru.allformine.afmcp.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class JSONRequest {
    public static void sendPost(String JSON, String url) {
        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(JSON, "UTF-8");
            request.addHeader("Content-Type", "application/json");
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode() != 204) {
                System.out.println("An error occurred when plugin tried to send log data.");
                System.out.println("JSON: "+JSON);
                System.out.println("Response: "+response.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
