package ru.allformine.afmcp.net;

import java.util.HashMap;
import java.util.Map;

public class NetUtils {
    public static String statusTextResponse(String status, String text) {
        return "{\"status\": \""+status+"\", \"text\": \""+text+"\"}";
    }
}
