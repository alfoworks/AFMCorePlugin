package ru.alfomine.afmcp;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PluginConfig {
    public static List<String> tabSortGroups = new ArrayList<>();

    public static int autoLoginHours;
    public static HashMap<String, Long> playerLastLoginTimestamps = new HashMap<>();
    public static HashMap<String, InetSocketAddress> playerLastLoginIps = new HashMap<>();

    public static int serverApiPort = 0;

    public static String serverId = "";
    public static String webhookApiUrl = "";

    public static List<String> hiddenCommandsList = new ArrayList<>();

    public static String lobbySpawnLocation = "";

    public static String tabListHeader = "";
    public static String tabListFooter = "";
    public static String tabListOnlineCount = "";
    public static String tabListCoordinates = "";
}
