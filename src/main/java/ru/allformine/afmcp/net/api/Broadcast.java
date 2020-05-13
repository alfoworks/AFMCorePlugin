package ru.allformine.afmcp.net.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.allformine.afmcp.PluginConfig;
import ru.allformine.afmcp.net.http.Requests;
import ru.allformine.afmcp.net.http.Response;

import java.util.ArrayList;

public class Broadcast {
	
	public static String broadcastPrefix;
	
	public static ArrayList<String> getBroadcasts() {
		String parameters = String.format("?id=%s", PluginConfig.serverId);
		
		Response resp = Requests.sendGet("https://localhost/broadcastapi" + parameters);
		
		if (resp == null || resp.response == null) {
			return new ArrayList<>();
		}
		
		ArrayList<String> out = new ArrayList<>();
		JsonObject json = new Gson().fromJson(resp.response, JsonObject.class);
		json.get("results").getAsJsonArray().forEach(elem -> {
			out.add(elem.getAsString());
		});
		
		broadcastPrefix = json.get("prefix").getAsString();
		
		return out;
	}
}
