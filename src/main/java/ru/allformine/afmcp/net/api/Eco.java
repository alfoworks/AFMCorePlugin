package ru.allformine.afmcp.net.api;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.http.GETResponse;
import ru.allformine.afmcp.net.http.Requests;

import java.util.OptionalInt;

public class Eco {
    private static ConfigurationNode configNode = AFMCorePlugin.getConfig().getNode("eco");
    private static String key = configNode.getNode("key").getString();
    private static String apiUrl = configNode.getNode("balanceApiUrl").getString() + "&key=" + key;
    private Player player;

    public Eco(Player player) {
        this.player = player;
    }

    public OptionalInt getBalance() {
        String url = apiUrl + "&act=get&nick=" + this.player.getName();
        GETResponse response = Requests.sendGet(url);
        if (response != null && response.responseCode == 200) {
            return OptionalInt.of(Integer.parseInt(response.response));
        } else {
            return OptionalInt.empty();
        }
    }

    public boolean increase(int count) {
        String url = apiUrl + "&act=increase&nick=" + this.player.getName() + "&var=" + count;
        GETResponse response = Requests.sendGet(url);
        return response != null && response.responseCode == 200;
    }

    public boolean decrease(int count) {
        String url = apiUrl + "&act=reduction&nick=" + this.player.getName() + "&var=" + count;
        GETResponse response = Requests.sendGet(url);
        return response != null && response.responseCode == 200;
    }

    public boolean reset() {
        String url = apiUrl + "&act=reset&nick=" + this.player.getName();
        GETResponse response = Requests.sendGet(url);
        return response != null && response.responseCode == 200;
    }
}
