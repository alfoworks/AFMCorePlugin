package ru.allformine.afmcp.vanish;

import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;

class VanishEffects {
    static void applyVanishEffect(Player player) {
        player.getWorld().spawnEntity(player.getWorld().createEntity(EntityTypes.LIGHTNING, player.getPosition()));
    }
}
