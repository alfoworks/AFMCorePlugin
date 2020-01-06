package ru.alfomine.afmcp.customitem;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.alfomine.afmcp.math.RayTrace;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomItemLaser extends CustomItem {
    private HashMap<Player, Integer> velocityPlayers = new HashMap<>();

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
        RayTrace rayTrace = new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
        ArrayList<Vector> positions = rayTrace.traverse(10, 0.01);
        for (Vector pos : positions) {
            player.getWorld().spawnParticle(Particle.REDSTONE, pos.getX(), pos.getY(), pos.getZ(), 1, 1);
        }
    }
}
