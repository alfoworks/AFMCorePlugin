package ru.alfomine.afmcp;

import org.bukkit.entity.Player;

import java.util.*;

public class PluginStatics {
    public static HashMap<Player, String> playerChestSet = new HashMap<>();
    public static HashMap<Player, String> playerChestPreset = new HashMap<>();

    public static List<Player> playerDel = new ArrayList<>();

    public static Set<Player> debugFlightParticlesPlayers = new HashSet<>();
    public static boolean debugRetranslateEnabled = false;
    public static String debugTranslatorKey = "trnsl.1.1.20180726T195057Z.f4e79197dd962469.1d406b138d18b3bda24f8cab2ecb69da10f6cfb2";

    public static long startTime = 0;

    public static boolean isServerRebooting = false;
}
