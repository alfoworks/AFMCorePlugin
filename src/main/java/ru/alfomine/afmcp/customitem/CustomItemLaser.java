package ru.alfomine.afmcp.customitem;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.alfomine.afmcp.math.RayTrace;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomItemLaser extends CustomItem {
    HashMap<Player, Long> cooldowns = new HashMap<>();

    @Override
    public String getId() {
        return "laser";
    }

    @Override
    public String getName() {
        return "LaserGun";
    }

    @Override
    public Material getMaterial() {
        return Material.STICK;
    }

    @Override
    public void onUse(Player player) {
        if (cooldowns.containsKey(player) && System.currentTimeMillis() < cooldowns.get(player) + 5000) {
            player.sendMessage("Кулдаун! Подождите 2 секунды перед новым использованием.");

            return;
        }

        RayTrace rayTrace = new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
        ArrayList<Vector> positions = rayTrace.traverse(50, 0.01);
        for (Vector pos : positions) {
            if (!player.getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).getType().isTransparent()) {
                break;
            }

            player.getWorld().spawnParticle(Particle.REDSTONE, pos.getX(), pos.getY(), pos.getZ(), 0, 1, 0, 0, 1);
        }

        cooldowns.put(player, System.currentTimeMillis());
    }
}
