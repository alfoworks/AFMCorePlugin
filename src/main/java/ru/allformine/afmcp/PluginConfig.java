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
	
	public static String tabListHeader = "";
	public static String tabListFooter = "";
	public static String tabListOnlineCount = "";
	public static String tabListCoordinates = "";
	
	public static String serverId;
	public static boolean broadcastEnabled;
}
