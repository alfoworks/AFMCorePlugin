package ru.allformine.afmcp.vanish;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.List;

public class VanishEffects {
    public static List<Entity> lightnings = new ArrayList<>();

    static void applyVanishEffect(Player player) {
        // Lightning

        Lightning lightning = (Lightning) player.getWorld().createEntity(EntityTypes.LIGHTNING, player.getPosition());
        lightning.setEffect(true);
        player.getWorld().spawnEntity(lightning);
        lightnings.add(lightning);

        // Bats

        List<Entity> batty = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            batty.add(player.getWorld().createEntity(EntityTypes.BAT, player.getPosition().add(0, 1, 0)));
        }

        player.getWorld().spawnEntities(batty);

        Task.builder().execute(() -> effectBatsCleanup(batty)).delayTicks(60).submit(Sponge.getPluginManager().getPlugin("afmcp").get());
    }

    private static void effectBatsCleanup(List<Entity> bats) {
        for (Entity bat : bats) {
            bat.getWorld().spawnParticles(ParticleEffect.builder().type(ParticleTypes.SMOKE).build(), bat.getLocation().getPosition());
            bat.remove();
        }
    }
}
