package ru.allformine.afmcp.lobby;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlayerState {
    private GameMode playerMode;
    private ItemStack[] playerInventory;
    private ItemStack[] playerInventoryArmor;
    private double playerHealth;
    private int playerHunger;
    private float playerXp;
    private Location<World> location;
    private boolean isFlying;

    PlayerState(Player player) {
        this.playerMode = player.gameMode().get();
        this.playerInventory = []; //[elem for elem in player.getInventory().iterator()];
        this.playerInventoryArmor = player.getInventory().getArmorContents();
        this.playerHealth = player.health().get();
        this.playerHunger = player.foodLevel().get();
        this.playerXp = 0;
        this.location = player.getLocation();
        this.isFlying = false;
    }

    void applyTo(Player player) {
        player.gameMode().set(playerMode);
        player.health().set(playerHealth);
        player.foodLevel().set(playerHunger);
        // player.setExp(playerXp);


        //player.getInventory().setContents(playerInventory);
        //player.getInventory().setArmorContents(playerInventoryArmor);

        player.setLocation(location);
        //player.setFlying(isFlying);
    }
}

