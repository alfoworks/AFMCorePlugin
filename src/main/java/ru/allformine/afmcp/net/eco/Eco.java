package ru.allformine.afmcp.net.eco;

import org.bukkit.plugin.java.JavaPlugin;
import ru.allformine.afmcp.net.Requests;

public class Eco {
    public static String getBalance(String nickname) {
        String resp = Requests.sendGet("https://allformine.ru/balanceapi?check=z5FXxPQqFJMKk5eGyTR2zhms6iAGwp&act=get&nick="
                + nickname);

        if ((resp != null) && !resp.equals("Hacking attempt!")) {
            return resp;
        } else {
            return null;
        }
    }

    public static boolean rem(String nickname, String val, JavaPlugin plugin) {
        String resp = Requests.sendGet(plugin.getConfig().getString("balanceapi.url") + "?check=z5FXxPQqFJMKk5eGyTR2zhms6iAGwp&act=rem&nick="
                + nickname + "&dif=" + val);

        return (resp != null) && !resp.equals("Hacking attempt!");
    }
}
