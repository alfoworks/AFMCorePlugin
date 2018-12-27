package ru.allformine.afmcp;

import org.bukkit.entity.Player;
import java.util.ArrayList;

public class References {
    static String[] notLoggedCommands = {"/g", "/t", "/l"};
    static String[] triggerWords = {"дюп", "баг", "краш", "нойра"};

    static ArrayList<Player> frozenPlayers = new ArrayList<>();

    public static boolean CFNTaskCustomTextRunnig = false;
    public static String CFNTaskCustomText = "";
}
