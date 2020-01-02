package ru.alfomine.afmcp.listeners;

import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;
import ru.alfomine.afmcp.PluginStatics;

public class CrapEventListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (PluginStatics.debugFlightParticlesPlayers.contains(event.getPlayer()) && event.getPlayer().isGliding()) {
            debugCrapElytra(event);
        }

        if (PluginStatics.debugRtxPlayers.contains(event.getPlayer()) && event.getFrom().getPitch() != event.getTo().getPitch() && event.getFrom().getYaw() == event.getTo().getYaw()) {
            debugCrapRtx(event);
        }
    }

    private void debugCrapElytra(PlayerMoveEvent event) {
        int particleCount = (int) Math.round(event.getFrom().distance(event.getTo()) * 10);
        Vector vector = event.getTo().getDirection().normalize().multiply(-0.5);

        event.getPlayer().getWorld().spawnParticle(Particle.CLOUD, event.getPlayer().getLocation(), particleCount, vector.getX(), vector.getY(), vector.getZ(), 0.1);
    }

    private void debugCrapRtx(PlayerMoveEvent event) {

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PluginStatics.debugFlightParticlesPlayers.remove(event.getPlayer());
        PluginStatics.debugRtxPlayers.remove(event.getPlayer());
    }
}
