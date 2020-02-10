package ru.alfomine.afmcp.lobby;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerState {
    private GameMode playerMode;
    private ItemStack[] playerInventory;
    private ItemStack[] playerInventoryArmor;
    private double playerHealth;
    private int playerHunger;
    private float playerXp;
    private Location location;
    private boolean isFlying;

    PlayerState(Player player) {
        this.playerMode = player.getGameMode();
        this.playerInventory = player.getInventory().getContents();
        this.playerInventoryArmor = player.getInventory().getArmorContents();
        this.playerHealth = player.getHealth();
        this.playerHunger = player.getFoodLevel();
        this.playerXp = player.getExp();
        this.location = player.getLocation();
        this.isFlying = player.isFlying();
    }

    void applyTo(Player player) {
        player.setGameMode(playerMode);
        player.setHealth(playerHealth);
        player.setFoodLevel(playerHunger);
        player.setExp(playerXp);

        player.getInventory().setContents(playerInventory);
        player.getInventory().setArmorContents(playerInventoryArmor);

        player.teleport(location);
        player.setFlying(isFlying);
    }
}
