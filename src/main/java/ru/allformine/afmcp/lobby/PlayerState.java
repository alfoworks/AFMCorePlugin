package ru.allformine.afmcp.lobby;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlayerState {
    private GameMode playerMode;
    private double playerHealth;
    private int playerHunger;
    private float playerXp;
    private Location<World> location;
    private boolean isFlying;

    PlayerState(Player player) {
        this.playerMode = player.gameMode().get();
        this.playerHealth = player.health().get();
        this.playerHunger = player.foodLevel().get();
        this.playerXp = player.get(Keys.EXPERIENCE_LEVEL).get();
        this.location = player.getLocation();
        this.isFlying = player.get(Keys.IS_FLYING).get();
    }

    void applyTo(Player player) {
        player.gameMode().set(playerMode);
        player.health().set(playerHealth);
        player.foodLevel().set(playerHunger);
        player.offer(Keys.TOTAL_EXPERIENCE, (int) playerXp);

        player.setLocation(location);
        player.offer(Keys.IS_FLYING, isFlying);
    }
}

