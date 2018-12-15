package ru.allformine.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class CFNChannelTask extends BukkitRunnable {
    private final JavaPlugin plugin;
    private Plugin Factions;

    public CFNChannelTask(JavaPlugin plugin, Plugin Factions) {
        this.plugin = plugin;
        this.Factions = Factions;
    }

    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            String str;

            if(Factions != null) {
                str = "nope xD";
            } else {
                str = ChatColor.BLUE+"SpaceUnion ЗБТ";
            }

            try {
                out.writeUTF(str);
            } catch(IOException e) {
                System.out.println("Error sending FactionsShow data.");
            }

            p.sendPluginMessage(plugin, "FactionsShow", b.toByteArray());
        }
    }
}
