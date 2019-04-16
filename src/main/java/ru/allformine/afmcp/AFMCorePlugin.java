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
import org.spongepowered.api.scheduler.Task;
import ru.allformine.afmcp.handlers.DiscordWebhookListener;
import ru.allformine.afmcp.net.discord.Discord;
import ru.allformine.afmcp.serverapi.HTTPServer;

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
    private Task apiServerTask;

    @Inject
    public static Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;
    private Path configFile;

    @Inject
    private void setLogger(Logger logger) {
        AFMCorePlugin.logger = logger;
    }

    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private static CommentedConfigurationNode configNode;

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        Sponge.getEventManager().registerListeners(this, new DiscordWebhookListener());

        configFile = configDir.resolve("config.conf");
        configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();

        configSetup();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
       Discord.sendMessageServer(Discord.MessageTypeServer.TYPE_SERVER_STARTED);

        apiServerTask = Task.builder().execute(new HTTPServer())
                .async().name("AFMCP APISERVER")
                .submit(this);
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        Discord.sendMessageServer(Discord.MessageTypeServer.TYPE_SERVER_STOPPED);

        apiServerTask.cancel();
    }

    private void configSetup() {
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException io) {
                io.printStackTrace();
            }
        }

        if (!Files.exists(configFile)) {
            try {
                //noinspection OptionalGetWithoutIsPresent
                Sponge.getAssetManager().getAsset(this, "config.conf").get().copyToFile(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        load();
    }

    private void load() {
        try {
            configNode = configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CommentedConfigurationNode getConfig(){
        return configNode;
    }
}
