package ru.alfomine.afmcp.customitem;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.alfomine.afmcp.math.RayTrace;

import java.util.ArrayList;

public class CustomItemLaser extends CustomItem {
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
        RayTrace rayTrace = new RayTrace(player.getEyeLocation().toVector().subtract(new Vector(0, 2, 0)), player.getEyeLocation().getDirection());
        ArrayList<Vector> positions = rayTrace.traverse(50, 0.01);
        for (Vector pos : positions) {
            if (!player.getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).getType().isTransparent()) {
                break;
            }

            player.getWorld().spawnParticle(Particle.REDSTONE, pos.getX(), pos.getY(), pos.getZ(), 0, 255, 255, 255, 1);
        }
    }
}
