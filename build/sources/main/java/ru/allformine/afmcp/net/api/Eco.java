package ru.allformine.afmcp.net.api;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.http.Requests;
import ru.allformine.afmcp.net.http.Response;

import java.util.OptionalInt;

public class Eco {
    private static ConfigurationNode configNode = AFMCorePlugin.getConfig().getNode("eco");
    private static String key = configNode.getNode("key").getString();
    private static String apiUrl = configNode.getNode("balanceApiUrl").getString() + "&key=" + key;
    private String nickname;
    public Eco(Player player) {
        this.nickname = player.getName();
    }

    public Eco(String nickname) {
        this.nickname = nickname;
    }

    private Response sendGet(String url) {
        Response response = Requests.sendGet(url);
        if (response != null && response.responseCode != 200) {
            System.out.println();
        }
        return response;
    }

    public OptionalInt getBalance() {
        String url = String.format("%s&act=%s&nick=%s", apiUrl, "get", this.nickname);
        Response response = Requests.sendGet(url);
        if (response != null && response.responseCode == 200) {
            return OptionalInt.of(Integer.parseInt(response.response));
        } else {
            return OptionalInt.empty();
        }
    }

    public boolean increase(int count) {
        String url = String.format("%s&act=%s&nick=%s&var=%s", apiUrl, "increase", this.nickname, count);
        Response response = Requests.sendGet(url);
        return response != null && response.responseCode == 200;
    }

    public boolean decrease(int count) {
        String url = String.format("%s&act=%s&nick=%s&var=%s", apiUrl, "reduction", this.nickname, count);
        Response response = Requests.sendGet(url);
        return response != null && response.responseCode == 200;
    }

    public boolean reset() {
        String url = String.format("%s&act=%s&nick=%s", apiUrl, "reset", this.nickname);
        Response response = Requests.sendGet(url);
        return response != null && response.responseCode == 200;
    }
}
