package ru.allformine.afmcp;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import ru.allformine.afmcp.handlers.EventListener;
import ru.allformine.afmcp.net.discord.Discord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    private Game game;
    @Inject
    public static Logger logger;

    @Inject @DefaultConfig(sharedRoot = true)
    private Path path;
    @Inject @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    public static ConfigurationNode config;

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        Sponge.getEventManager().registerListeners(this, new EventListener());

        try {
            if (!Files.exists(path)) {
                Sponge.getAssetManager().getAsset(this, "default.conf").get().copyToFile(path);
            }
            config = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        Discord.sendMessageServer(Discord.MessageTypeServer.TYPE_SERVER_STARTED);

        logger.info(AFMCorePlugin.config.getString("discord.webhooks.url_lvl1"));
    }
}
