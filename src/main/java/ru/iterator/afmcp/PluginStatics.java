package ru.iterator.afmcp;

import org.bukkit.entity.Player;

import java.util.*;

public class PluginStatics {
    public static HashMap<Player, String> playerChestSet = new HashMap<>();
    public static HashMap<Player, String> playerChestPreset = new HashMap<>();

    public static List<Player> playerDel = new ArrayList<>();

    public static Set<Player> debugFlightParticlesPlayers = new HashSet<>();
    public static Set<Player> debugRtxPlayers = new HashSet<>();
    public static HashMap<Player, Integer> debugRtxTasks = new HashMap<>();
}
