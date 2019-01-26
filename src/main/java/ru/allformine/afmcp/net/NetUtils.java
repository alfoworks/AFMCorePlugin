package ru.allformine.afmcp.net;

import java.util.HashMap;
import java.util.Map;

public class NetUtils {
    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }

        return result;
    }
}
