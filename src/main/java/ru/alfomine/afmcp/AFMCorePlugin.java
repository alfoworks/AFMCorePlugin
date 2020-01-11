package ru.alfomine.afmcp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.alfomine.afmcp.commands.*;
import ru.alfomine.afmcp.customitem.CustomItem;
import ru.alfomine.afmcp.customitem.CustomItemLaser;
import ru.alfomine.afmcp.customitem.CustomItemManager;
import ru.alfomine.afmcp.customitem.CustomItemVelocitySnowball;
import ru.alfomine.afmcp.listeners.*;
import ru.alfomine.afmcp.lobby.Lobby;
import ru.alfomine.afmcp.net.webhookapi.MessageTypeServer;
import ru.alfomine.afmcp.net.webhookapi.WebhookApi;
import ru.alfomine.afmcp.packetlisteners.TranslatePacketListener;
import ru.alfomine.afmcp.serverapi.APIServer;
import ru.alfomine.afmcp.tablist.WrappedTabList;
import ru.alfomine.afmcp.util.LocationUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class AFMCorePlugin extends JavaPlugin {
    private static AFMCorePlugin plugin;
    public static FileConfiguration config;
    public static WrappedTabList tabList;
    private static Logger logger = Bukkit.getLogger();
    ProtocolManager protocolManager;
    public Lobby lobby;

    public static void log(String message, Level level) {
        logger.log(level, String.format("AFMCP [%s]: %s", level.getName(), message));
    }

    public static AFMCorePlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new TranslatePacketListener(this, PacketType.Play.Server.CHAT));

        CustomItemManager.addCustomItem(new CustomItemVelocitySnowball());
        CustomItemManager.addCustomItem(new CustomItemLaser());

        lobby = new Lobby();

        getServer().getPluginManager().registerEvents(new MainEventListener(), this);
        getServer().getPluginManager().registerEvents(new CrapEventListener(), this);
        getServer().getPluginManager().registerEvents(new WebhookApiListener(), this);
        getServer().getPluginManager().registerEvents(new CustomItemListener(), this);
        getServer().getPluginManager().registerEvents(new ShitEvenListener(), this);
        getServer().getPluginManager().registerEvents(lobby, this);

        for (CustomItem item : CustomItemManager.items) {
            getServer().getPluginManager().registerEvents(item, this);
        }

        plugin = this;

        this.saveDefaultConfig();
        config = this.getConfig();
        configReload();

        this.getCommand("createpreset").setExecutor(new CommandCreatePreset());
        this.getCommand("setchest").setExecutor(new CommandSetChest());
        this.getCommand("deletepreset").setExecutor(new CommandDeletePreset());
        this.getCommand("unsetchest").setExecutor(new CommandUnsetChest());
        this.getCommand("afmcp").setExecutor(new CommandAFMCP());
        this.getCommand("rawbc").setExecutor(new CommandRawBC());
        this.getCommand("afmrestart").setExecutor(new CommandAFMRestart());
        this.getCommand("customitem").setExecutor(new CommandCustomItem());
        this.getCommand("ds").setExecutor(new CommandDS());
        this.getCommand("lobby").setExecutor(new CommandLobby());

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            ConfigurationSection section = config.getConfigurationSection("chests");

            if (section == null) {
                return;
            }

            for (String key : section.getKeys(false)) {
                Block chestBlock = LocationUtil.fromString(config.getString("chests." + key)).getBlock();
                Block presetBlock = LocationUtil.fromString(config.getString("presets." + key)).getBlock();

                if ((chestBlock.getState() instanceof Chest) && (presetBlock.getState() instanceof Chest)) {
                    ((Chest) chestBlock.getState()).getBlockInventory().setContents(((Chest) presetBlock.getState()).getBlockInventory().getContents());
                }
            }
        }, 0, 400); //216000

        tabList = new WrappedTabList();
        PluginStatics.startTime = System.currentTimeMillis();

        Bukkit.getServer().getScheduler().runTaskAsynchronously(this, new APIServer());
        Bukkit.getServer().getScheduler().runTaskTimer(this, new DiscordNotifyTask(), 0L, 600L);

        WebhookApi.sendServerMessage(MessageTypeServer.SERVER_STARTED);
    }

    // TODO Улучшить обработку конфигов. Почистить от старого говна (ChestRefill) и добавить ООП ко всему этому ужасу.
    public void configReload() {
        PluginConfig.tabSortGroups = config.getStringList("tabSortGroups").toArray(new String[]{});
        PluginConfig.serverApiPort = config.getInt("server_api.port");

        PluginConfig.serverId = config.getString("webhook_api.serverId");
        PluginConfig.webhookApiUrl = config.getString("webhook_api.url");
        PluginConfig.hiddenCommandsList = config.getStringList("webhook_api.hiddenCommands");

        PluginConfig.lobbySpawnLocation = config.getString("lobby.spawnLocation");
    }

    public void configSave() {
        config.set("lobby.spawnLocation", PluginConfig.lobbySpawnLocation);

        saveConfig();
    }

    @Override
    public void onDisable() {
        WebhookApi.sendServerMessage(!PluginStatics.isServerRebooting ? MessageTypeServer.SERVER_STOPPED : MessageTypeServer.SERVER_RESTARTING);
    }
}
