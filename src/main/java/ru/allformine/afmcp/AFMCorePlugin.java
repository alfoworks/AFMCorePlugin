package ru.allformine.afmcp;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import ru.allformine.afmcp.handlers.EventListener;
import ru.allformine.afmcp.net.discord.Discord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Plugin(
        id = "afmcp",
        name = "AFMCorePlugin",
        description = "A plugin for a couple of random tasks.",
        url = "http://allformine.ru",
        authors = {
                "Iterator"
        }
)
public class AFMCorePlugin {
    @Inject
    public static Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private Path configFile = Paths.get(configDir + "/config.conf");

    private ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
    private static CommentedConfigurationNode configNode;

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        Sponge.getEventManager().registerListeners(this, new EventListener());

        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        setup();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info(configNode.getString("discord.webhooks.url_lvl1"));

        Discord.sendMessageServer(Discord.MessageTypeServer.TYPE_SERVER_STARTED);
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        Discord.sendMessageServer(Discord.MessageTypeServer.TYPE_SERVER_STOPPED);
    }

    // Всякая хуйня для конфигов
    private void setup() {
        if (!Files.exists(configFile)) {
            try {
                Files.createFile(configFile);
                Sponge.getAssetManager().getAsset(this, "default.conf").get().copyToFile(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            load();
        }
    }

    private void load() {
        try {
            configNode = configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            configLoader.save(configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CommentedConfigurationNode getConfig() {
        return configNode;
    }
}
