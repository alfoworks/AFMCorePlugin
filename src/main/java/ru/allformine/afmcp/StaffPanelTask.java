package ru.allformine.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class StaffPanelTask extends BukkitRunnable {
    private final JavaPlugin plugin;

    public StaffPanelTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission("afmcp.staff.staffpanel")) {

            }
        }
    }
}
