package ru.allformine.afmcp;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

import java.util.ArrayList;

public class References {
    public static VanishManager vmng;
    static ArrayList<Player> frozenPlayers = new ArrayList<>();

    public static boolean log = true;

    static ChatColor colors[] = {ChatColor.AQUA, ChatColor.BLACK, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE, ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.GRAY, ChatColor.GREEN, ChatColor.ITALIC, ChatColor.LIGHT_PURPLE, ChatColor.RED};
}
