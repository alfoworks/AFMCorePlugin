package ru.alfomine.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.alfomine.afmcp.commands.*;
import ru.alfomine.afmcp.listeners.CrapEventListener;
import ru.alfomine.afmcp.listeners.MainEventListener;
import ru.alfomine.afmcp.listeners.TabListEventListener;
import ru.alfomine.afmcp.listeners.WebhookApiListener;
import ru.alfomine.afmcp.serverapi.APIServer;
import ru.alfomine.afmcp.tablist.WrappedTabList;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class AFMCorePlugin extends JavaPlugin {
    private static AFMCorePlugin plugin;
    public static FileConfiguration config;
    public static WrappedTabList tabList;
    private static Logger logger = Bukkit.getLogger();

    public static void log(String message, Level level) {
        logger.log(level, String.format("AFMCP [%s]: %s", level.getName(), message));
    }

    public static AFMCorePlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new MainEventListener(), this);
        getServer().getPluginManager().registerEvents(new TabListEventListener(), this);
        getServer().getPluginManager().registerEvents(new CrapEventListener(), this);
        getServer().getPluginManager().registerEvents(new WebhookApiListener(), this);

        plugin = this;

        this.saveDefaultConfig();
        config = this.getConfig();
        configReload();

        this.getCommand("createpreset").setExecutor(new CommandCreatePreset());
        this.getCommand("setchest").setExecutor(new CommandSetChest());
        this.getCommand("deletepreset").setExecutor(new CommandDeletePreset());
        this.getCommand("unsetchest").setExecutor(new CommandUnsetChest());
        this.getCommand("afmcp").setExecutor(new CommandSOICP());

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            ConfigurationSection section = config.getConfigurationSection("chests");

            if (section == null) {
                return;
            }

            for(String key : section.getKeys(false)) {
                Block chestBlock = LocationUtil.fromString(config.getString("chests." + key)).getBlock();
                Block presetBlock = LocationUtil.fromString(config.getString("presets." + key)).getBlock();

                if ((chestBlock.getState() instanceof Chest) && (presetBlock.getState() instanceof Chest)) {
                    ((Chest) chestBlock.getState()).getBlockInventory().setContents(((Chest) presetBlock.getState()).getBlockInventory().getContents());
                }
            }
        }, 0, 216000); //216000

        tabList = new WrappedTabList();
        PluginStatics.startTime = System.currentTimeMillis();

        Bukkit.getServer().getScheduler().runTaskAsynchronously(this, new APIServer());
    }

    // TODO Улучшить обработку конфигов. Почистить от старого говна (ChestRefill) и добавить ООП ко всему этому ужасу.
    public void configReload() {
        PluginConfig.tabSortGroups = config.getStringList("tabSortGroups").toArray(new String[]{});
        PluginConfig.serverApiPort = config.getInt("server_api.port");

        PluginConfig.serverId = config.getString("webhook_api.serverId");
        PluginConfig.webhookApiUrl = config.getString("webhook_api.url");
    }

    public void configSave() {
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
