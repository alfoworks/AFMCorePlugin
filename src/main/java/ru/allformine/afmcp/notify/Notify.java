package ru.allformine.afmcp.notify;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.allformine.afmcp.AFMCorePlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Notify {
    public static void notifyAll(String message) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            try {
                out.writeUTF(message);
            } catch (IOException e) {
                System.out.println("Error sending FactionsShow data.");
            }

            p.sendPluginMessage(AFMCorePlugin.getPlugin(), "FactionsShow", b.toByteArray());
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(AFMCorePlugin.getPlugin(), () -> {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                p.sendPluginMessage(AFMCorePlugin.getPlugin(), "FactionsShow", Util.getEmptyString());
            }
        }, 200L);
    }

    public static void notifyPlayer(String message, Player p) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Error sending FactionsShow data.");
        }

        p.sendPluginMessage(AFMCorePlugin.getPlugin(), "FactionsShow", b.toByteArray());

        Bukkit.getScheduler().scheduleSyncDelayedTask(AFMCorePlugin.getPlugin(), () -> p.sendPluginMessage(AFMCorePlugin.getPlugin(), "FactionsShow", Util.getEmptyString()), 200L);
    }
}
