package ru.allformine.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Notify {
    public static void NotifyAll(String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            try {
                out.writeUTF(message);
            } catch (IOException e) {
                System.out.println("Error sending FactionsShow data.");
            }

            p.sendPluginMessage(AFMCorePlugin.getPlugin(), "FactionsShow", b.toByteArray());
            p.playSound(p.getLocation(), Sound.NOTE_PLING, 10, 1);
            p.playSound(p.getLocation(), Sound.NOTE_PLING, 10, -5);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(AFMCorePlugin.getPlugin(), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);

                try {
                    out.writeUTF("");
                } catch (IOException e) {
                    System.out.println("Error sending FactionsShow data.");
                }

                p.sendPluginMessage(AFMCorePlugin.getPlugin(), "FactionsShow", b.toByteArray());
            }
        }, 200L);
    }
}
