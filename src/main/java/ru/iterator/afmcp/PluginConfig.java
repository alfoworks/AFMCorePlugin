package ru.iterator.afmcp;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class PluginConfig {
    public static String[] tabSortGroups = new String[]{};

    public static int autoLoginHours;
    public static HashMap<String, Long> playerLastLoginTimestamps = new HashMap<>();
    public static HashMap<String, InetSocketAddress> playerLastLoginIps = new HashMap<>();
}
