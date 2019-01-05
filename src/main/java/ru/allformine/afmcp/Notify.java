package ru.allformine.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class Notify {
    static void NotifyAll(String message) {
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
                p.sendPluginMessage(AFMCorePlugin.getPlugin(), "FactionsShow", new byte[] {});
            }
        }, 200L);
    }

    static void NotifyPlayer(Player p, String message) {
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

        Bukkit.getScheduler().scheduleSyncDelayedTask(AFMCorePlugin.getPlugin(), () -> p.sendPluginMessage(AFMCorePlugin.getPlugin(), "FactionsShow", new byte[] {}), 200L);
    }
}
