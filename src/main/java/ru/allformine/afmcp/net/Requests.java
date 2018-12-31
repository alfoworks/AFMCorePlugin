package ru.allformine.afmcp.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Requests {
    public static void sendPost(String JSON, String url) {
        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(JSON, "UTF-8");
            request.addHeader("Content-Type", "application/json");
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 204) {
                System.out.println("An error occurred when plugin tried to send log data.");
                System.out.println("JSON: " + JSON);
                System.out.println("Response: " + response.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String sendGet(String url) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        try {
            HttpResponse response = client.execute(request);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
