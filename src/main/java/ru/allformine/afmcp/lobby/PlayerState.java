package ru.allformine.afmcp.lobby;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlayerState {
    private GameMode playerMode;
    private double playerHealth;
    private int playerHunger;
    private float playerXp;
    private Location<World> location;
    private boolean isFlying;
    private boolean isFlyingElytra;
    private ItemStack itemInOffHand;

    PlayerState(Player player) {
        this.playerMode = player.gameMode().get();
        this.playerHealth = player.health().get();
        this.playerHunger = player.foodLevel().get();
        this.playerXp = player.get(Keys.EXPERIENCE_LEVEL).get();
        this.location = player.getLocation();
        this.isFlying = player.get(Keys.IS_FLYING).get();
        this.itemInOffHand = player.getItemInHand(HandTypes.OFF_HAND).orElse(ItemStack.empty());
        this.isFlyingElytra = player.get(Keys.IS_ELYTRA_FLYING).get();
    }

    void applyTo(Player player) {
        player.gameMode().set(playerMode);
        player.health().set(playerHealth);
        player.foodLevel().set(playerHunger);
        player.offer(Keys.TOTAL_EXPERIENCE, (int) playerXp);
        player.setLocation(location);
        player.offer(Keys.IS_FLYING, isFlying);
        player.setItemInHand(HandTypes.OFF_HAND, itemInOffHand);
        player.offer(Keys.IS_ELYTRA_FLYING, isFlyingElytra);
    }
}

