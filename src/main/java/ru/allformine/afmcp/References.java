package ru.allformine.afmcp;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class References {
    public static boolean log = true;
    static ArrayList<Player> frozenPlayers = new ArrayList<>();
    static ChatColor colors[] = {ChatColor.AQUA, ChatColor.BLACK, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE, ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.GRAY, ChatColor.GREEN, ChatColor.ITALIC, ChatColor.LIGHT_PURPLE, ChatColor.RED};

    static HashMap<Player, String> playerCurrentMusic = new HashMap<>();
    static HashMap<Player, String> playerCurrentNamedRegion = new HashMap<>();
}
