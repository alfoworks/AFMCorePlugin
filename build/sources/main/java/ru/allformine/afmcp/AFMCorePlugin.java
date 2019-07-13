package ru.allformine.afmcp;

import com.google.inject.Inject;
import io.netty.channel.ChannelFuture;
import net.minecraft.network.NetworkSystem;
import net.minecraft.server.MinecraftServer;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import ru.allformine.afmcp.commands.RawBCCommand;
import ru.allformine.afmcp.commands.RestartCommand;
import ru.allformine.afmcp.commands.TokensCommand;
import ru.allformine.afmcp.commands.VipCommand;
import ru.allformine.afmcp.handlers.DiscordWebhookListener;
import ru.allformine.afmcp.handlers.FactionEventListener;
import ru.allformine.afmcp.handlers.VanishEventListener;
import ru.allformine.afmcp.jumppad.JumpPadEventListener;
import ru.allformine.afmcp.net.api.Webhook;
import ru.allformine.afmcp.packetlisteners.ScreenshotListener;
import ru.allformine.afmcp.serverapi.HTTPServer;
import ru.allformine.afmcp.test.PacketHandler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
    public static boolean serverRestart = false;
    @Inject
    public static Logger logger;
    private static CommentedConfigurationNode configNode;

    public static HTTPServer apiServer;
    private Task apiServerTask;

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
        Sponge.getEventManager().registerListeners(this, new JumpPadEventListener());
        Sponge.getEventManager().registerListeners(this, new FactionEventListener());

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
                        GenericArguments.optional(GenericArguments.string(Text.of("selectedVip")))
                )
                .build();

        Sponge.getCommandManager().register(this, vipCommandSpec, "vip");

        CommandSpec rawBCspec = CommandSpec.builder()
                .description(Text.of("Отправить текст в чат в таком виде, в каком вы его пишете."))
                .executor(new RawBCCommand())
                .arguments(
                        GenericArguments.remainingJoinedStrings(Text.of("text"))
                )
                .build();

        Sponge.getCommandManager().register(this, rawBCspec, "rawbc");
    }

    @Listener
    public void init(GameInitializationEvent event) {
        PacketChannels.SCREENSHOT = Sponge.getGame()
                .getChannelRegistrar()
                .createRawChannel(this, "AN3234234A");

        PacketChannels.FACTIONS = Sponge.getGame()
                .getChannelRegistrar()
                .createRawChannel(this, "factions");

        PacketChannels.SCREENSHOT.addListener(new ScreenshotListener());

        try {
            NetworkSystem networkSystem = ((MinecraftServer) Sponge.getServer()).getNetworkSystem();
            Field endpointsField = networkSystem.getClass().getDeclaredField("field_151274_e");
            endpointsField.setAccessible(true);
            List<ChannelFuture> endpoints = (List<ChannelFuture>) endpointsField.get(networkSystem);
            for (ChannelFuture channelFuture : endpoints) {
                channelFuture.channel().pipeline().addFirst("PacketHandlerXXX", new PacketHandler());

                for (String name : channelFuture.channel().pipeline().names()) {
                    logger.info(name);
                }
            }
        } catch (Exception e) {
            logger.error("Can't attach PacketHandler!");
            e.printStackTrace();
        }
    }

    @Listener
    public void onServerStart(GameAboutToStartServerEvent event) {
        startTime = System.currentTimeMillis();

        //Discord.sendMessageServer(Discord.MessageTypeServer.TYPE_SERVER_STARTED);
        Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_STARTED);

        apiServer = new HTTPServer();

        apiServerTask = Task.builder().execute(apiServer)
                .async().name("AFMCP APISERVER")
                .submit(this);
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        //Discord.sendMessageServer(Discord.MessageTypeServer.TYPE_SERVER_STOPPED);
        if (serverRestart) {
            Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_RESTARTING);
        } else {
            Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_STOPPED);
        }
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
