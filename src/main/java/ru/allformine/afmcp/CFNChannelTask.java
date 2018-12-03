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
    ArrayList<ChatColor> colors = new ArrayList<ChatColor>();
    Random randomizer;

    public CFNChannelTask(JavaPlugin plugin, Plugin Factions) {
        this.plugin = plugin;
        this.Factions = Factions;

        this.colors.add(ChatColor.BLUE);
        this.colors.add(ChatColor.WHITE);
        this.colors.add(ChatColor.DARK_BLUE);
        this.colors.add(ChatColor.DARK_PURPLE);
        this.colors.add(ChatColor.LIGHT_PURPLE);
        this.colors.add(ChatColor.YELLOW);
        this.colors.add(ChatColor.AQUA);
        this.colors.add(ChatColor.BLACK);
        this.colors.add(ChatColor.DARK_AQUA);
        this.colors.add(ChatColor.DARK_GRAY);
        this.colors.add(ChatColor.DARK_GREEN);
        this.colors.add(ChatColor.DARK_RED);
        this.colors.add(ChatColor.GOLD);
        this.colors.add(ChatColor.GRAY);
        this.colors.add(ChatColor.GREEN);
        this.colors.add(ChatColor.RED);

        this.randomizer = new Random();
    }

    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            String str;

            if(Factions != null) {
                str = "nope xD";
            } else {
                str = colors.get(randomizer.nextInt(colors.size()))+"SpaceUnion Beta";
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
