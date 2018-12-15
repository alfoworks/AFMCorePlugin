package ru.allformine.afmcp.net;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class request {
    public static void sendPost(String JSON, String url) {
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead

        try {
            HttpPost request = new HttpPost(url);
            StringEntity params =new StringEntity(JSON);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
