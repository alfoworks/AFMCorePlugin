package ru.allformine.afmcp.net.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;

class ServerUtils {
    static void responseString(HttpExchange exchange, int httpCode, String string) {
        byte[] bytes = string.getBytes();

        try {
            exchange.sendResponseHeaders(httpCode, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);

            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
