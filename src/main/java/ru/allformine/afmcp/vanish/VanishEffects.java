package ru.allformine.afmcp.vanish;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.List;

class VanishEffects {
    static void applyVanishEffect(Player player) {
        // Lightning

        player.getWorld().spawnEntity(player.getWorld().createEntity(EntityTypes.LIGHTNING, player.getPosition())); //TODO убрать дамаг молнии

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
