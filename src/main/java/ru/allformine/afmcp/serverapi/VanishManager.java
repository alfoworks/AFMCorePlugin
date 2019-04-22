package ru.allformine.afmcp.serverapi;

import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;

public class VanishManager{
    private static HashMap<Player,Boolean> vanishedPlayers;
    public static final String vanishPermission = "str";
    public static boolean isVanished(Player player){
        return vanishedPlayers.containsKey(player);
    }
    public static void vanishPlayer(Player player){
        vanishedPlayers.put(player,true);
    }
    public static void unvanishPlayer(Player player){
        vanishedPlayers.remove(player);
    }
}