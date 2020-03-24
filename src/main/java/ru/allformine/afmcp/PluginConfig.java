package ru.allformine.afmcp;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PluginConfig {
    public static boolean lobbyEnabled;
    public static String lobbyId;
    public static Location<World> lobbySpawn;

    public static String motdDescription;

    public static ConfigurationNode tablistSorting;
}
