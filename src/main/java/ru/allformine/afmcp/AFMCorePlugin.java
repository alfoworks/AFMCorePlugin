package ru.allformine.afmcp;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import ru.allformine.afmcp.commands.RestartCommand;
import ru.allformine.afmcp.commands.TokensCommand;
import ru.allformine.afmcp.commands.VipCommand;
import ru.allformine.afmcp.handlers.DiscordWebhookListener;
import ru.allformine.afmcp.handlers.VanishEventListener;
import ru.allformine.afmcp.net.discord.Discord;
import ru.allformine.afmcp.serverapi.HTTPServer;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static jdk.nashorn.internal.objects.NativeString.trim;

@Plugin(
        id = "afmcp",
        name = "AFMCorePlugin",
        description = "A plugin for a couple of random tasks.",
        url = "http://allformine.ru",
        authors = {
                "Iterator, HeroBrine1st_Erq"
        }
)
public class AFMCorePlugin {
    @Inject
    public static Logger logger;
    private static CommentedConfigurationNode configNode;
    private Task apiServerTask;

    public static Map<String,ChannelBinding.RawDataChannel> channel = new HashMap<>();

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;
    private Path configFile;
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    public static long startTime = 0;

    public static CommentedConfigurationNode getConfig() {
        return configNode;
    }

    @Inject
    private void setLogger(Logger logger) {
        AFMCorePlugin.logger = logger;
    }

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        Sponge.getEventManager().registerListeners(this, new DiscordWebhookListener());
        Sponge.getEventManager().registerListeners(this, new VanishEventListener());

        configFile = configDir.resolve("config.conf");
        configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();

        configSetup();

        CommandSpec restartCommandSpec = CommandSpec.builder()
                .description(Text.of("Команда для перезагрузки сервера, единственная верная."))
                .permission("afmcp.admin")
                .executor(new RestartCommand())
                .build();

        Sponge.getCommandManager().register(this, restartCommandSpec, "afmrestart", "servrestart");

        CommandSpec tokensCommandSpec = CommandSpec.builder()
                .description(Text.of("Команда для получения количества токенов."))
                .executor(new TokensCommand())
                .build();

        Sponge.getCommandManager().register(this, tokensCommandSpec, "tokens");

        CommandSpec vipCommandSpec = CommandSpec.builder()
                .description(Text.of("Команда для покупки привелегий."))
                .executor(new VipCommand())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("selectedVip")))
                )
                .build();

        Sponge.getCommandManager().register(this, vipCommandSpec, "vip");
    }

    @Listener
    public void init(GameInitializationEvent event){
        channel.put("screenshot", Sponge.getGame()
                .getChannelRegistrar()
                .createRawChannel(this, "C234Fb"));

        //TODO: ебитесь с этим сами
//        channel.get("screenshot").addListener(Platform.Type.SERVER, (buf, con, side) -> {
//            byte[] message = buf.readByteArray();
//            if (apiServerTask..playerScreenshotData.get(player) != null) {
//                message = trim(message);
//                message = Arrays.copyOf(message, message.length - 1);
//            }
//        });
    }

    @Listener
    public void onServerStart(GameAboutToStartServerEvent event) {
        startTime = System.currentTimeMillis();

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
}
