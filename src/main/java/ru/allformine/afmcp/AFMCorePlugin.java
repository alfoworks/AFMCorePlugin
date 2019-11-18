package ru.allformine.afmcp;

import com.google.inject.Inject;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import ru.allformine.afmcp.commands.*;
import ru.allformine.afmcp.jumppad.JumpPadEventListener;
import ru.allformine.afmcp.listeners.DiscordWebhookListener;
import ru.allformine.afmcp.listeners.FactionEventListener;
import ru.allformine.afmcp.listeners.VanishEventListener;
import ru.allformine.afmcp.net.api.Webhook;
import ru.allformine.afmcp.packetlisteners.ScreenshotListener;
import ru.allformine.afmcp.serverapi.HTTPServer;
import ru.allformine.afmcp.vanish.VanishPacketListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "afmcp",
        name = "AFMCorePlugin",
        description = "A plugin for a couple of random tasks.",
        version = "0.7",
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

        CommandSpec vanishSpec = CommandSpec.builder()
                .description(Text.of("Vanish"))
                .permission("afmcp.vanish")
                .executor(new VanishCommand())
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("subcmd")))
                )
                .build();

        Sponge.getCommandManager().register(this, vanishSpec, "vanish", "v");

        CommandSpec vanishNoInteractSpec = CommandSpec.builder()
                .description(Text.of("No interact"))
                .permission("afmcp.vanish")
                .executor(new VanishNoInteractCommand())
                .build();

        Sponge.getCommandManager().register(this, vanishNoInteractSpec, "ni");
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
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        startTime = System.currentTimeMillis();

        //Discord.sendMessageServer(Discord.MessageTypeServer.TYPE_SERVER_STARTED);
        Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_STARTED);

        apiServer = new HTTPServer();

        apiServerTask = Task.builder().execute(apiServer)
                .async().name("AFMCP APISERVER")
                .submit(this);

        Optional<PacketGate> optional = Sponge.getServiceManager().provide(PacketGate.class);
        optional.ifPresent(packetGate -> packetGate.registerListener(new VanishPacketListener(), PacketListener.ListenerPriority.FIRST, SPacketPlayerListItem.class));
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
