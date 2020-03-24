package ru.allformine.afmcp;

import com.google.inject.Inject;
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
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import ru.allformine.afmcp.commands.*;
import ru.allformine.afmcp.jumppad.JumpPadEventListener;
import ru.allformine.afmcp.listeners.*;
import ru.allformine.afmcp.lobby.LobbyCommon;
import ru.allformine.afmcp.lobby.LobbySOI;
import ru.allformine.afmcp.lobby.LobbyVanilla;
import ru.allformine.afmcp.net.api.Webhook;
import ru.allformine.afmcp.tablist.UpdateTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Plugin(
        id = "afmcp",
        name = "AFMCorePlugin",
        description = "A plugin for a couple of random tasks.",
        version = "0.7",
        url = "http://allformine.ru",
        authors = {
                "Iterator, HeroBrine1st_Erq"
        },
        dependencies = {@Dependency(id = "spotlin", version = "0.2.0")}
)
public class AFMCorePlugin {
    public static boolean serverRestart = false;
    @Inject
    public static Logger logger;
    private static CommentedConfigurationNode configNode;
    public static LobbyCommon[] lobbies = new LobbyCommon[]{new LobbySOI(), new LobbyVanilla()};
    public static LobbyCommon currentLobby;
    public static boolean debugSwitch = false;
    public static AFMCorePlugin instance;
    public static Task lagTask;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;
    private Path configFile;
    private static ConfigurationLoader<CommentedConfigurationNode> configLoader;

    public static CommentedConfigurationNode getConfig() {
        return configNode;
    }

    @Inject
    private void setLogger(Logger logger) {
        AFMCorePlugin.logger = logger;
    }

    public static void saveConfig() {
        try {
            configLoader.save(configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void init(GameInitializationEvent event) {
        PacketChannels.FACTIONS = Sponge.getGame()
                .getChannelRegistrar()
                .createRawChannel(this, "factions");
        PacketChannels.MESSAGING = Sponge.getGame()
                .getChannelRegistrar()
                .createRawChannel(this, "afmmessaging");
    }

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        instance = this;

        Sponge.getEventManager().registerListeners(this, new DiscordWebhookListener());
        Sponge.getEventManager().registerListeners(this, new JumpPadEventListener());
        Sponge.getEventManager().registerListeners(this, new FactionEventListener());
        Sponge.getEventManager().registerListeners(this, new TestEventListener());
        Sponge.getEventManager().registerListeners(this, new ChatEventListener());
        Sponge.getEventManager().registerListeners(this, new MOTDEventListener());

        configFile = configDir.resolve("config.conf");
        configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();

        configSetup();

        CommandSpec kotlinTestSpec = CommandSpec.builder()
                .executor(new KotlinTestCommand())
                .build();

        Sponge.getCommandManager().register(this, kotlinTestSpec, "kotlintest");

        CommandSpec restartCommandSpec = CommandSpec.builder()
                .description(Text.of("Команда для перезагрузки сервера, единственная верная."))
                .permission("afmcp.admin")
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("minutes"))))
                .executor(new RestartCommand())
                .build();

        Sponge.getCommandManager().register(this, restartCommandSpec, "afmrestart", "servrestart", "restart");

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

        CommandSpec debugSpec = CommandSpec.builder()
                .description(Text.of("Debug switch"))
                .executor(new DebugCommand())
                .build();

        Sponge.getCommandManager().register(this, debugSpec, "debugswitch");

        CommandSpec lobbySpec = CommandSpec.builder()
                .description(Text.of("Лобби"))
                .executor(new LobbyCommand())
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("subcommand"))))
                .build();

        Sponge.getCommandManager().register(this, lobbySpec, "lobby");

        CommandSpec tickIntervalSpec = CommandSpec.builder()
                .description(Text.of("Fuck"))
                .executor(new TickIntervalCommand())
                .arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("interval"))))
                .build();

        Sponge.getCommandManager().register(this, tickIntervalSpec, "tickinterval", "ti");

        CommandSpec messageSpec = CommandSpec.builder()
                .description(Text.of("(MessagingAPI) отправить собщение всем"))
                .executor(new MessageCommand())
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
                        GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("message"))))
                .build();

        Sponge.getCommandManager().register(this, messageSpec, "message", "msg");

        CommandSpec messagePlayerSpec = CommandSpec.builder()
                .description(Text.of("(MessagingAPI) отправить собщение игроку"))
                .executor(new MessageCommand())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
                        GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("message"))))
                .build();

        Sponge.getCommandManager().register(this, messageSpec, "messageplayer", "msgplayer");

        if (PluginConfig.lobbyId != null) {
            for (LobbyCommon lobby : lobbies) {
                if (lobby.getLobbyId().equals(PluginConfig.lobbyId)) {
                    Sponge.getEventManager().registerListeners(this, lobby);
                    currentLobby = lobby;
                }
            }
        }
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        if (serverRestart) {
            Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_RESTARTING);
        } else {
            Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_STOPPED);
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_STARTED);
        Task.builder()
                .execute(new UpdateTask())
                .intervalTicks(20)
                .name("TabList Update Task")
                .submit(this);



        if (!PluginConfig.lobbyEnabled) {
            return;
        }

        if (Objects.equals(PluginConfig.lobbyId, "")) {
            logger.error("Lobby id is empty!");
            PluginConfig.lobbyId = null;
        }

        PluginConfig.lobbySpawn = new LocationSerializer().deserialize(configNode.getNode("lobby").getNode("location"));

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

        PluginConfig.lobbyEnabled = configNode.getNode("lobby").getNode("enabled").getBoolean();
        PluginConfig.lobbyId = configNode.getNode("lobby").getNode("id").getString();
        PluginConfig.motdDescription = configNode.getNode("motd").getNode("description").getString();
        PluginConfig.tablistSorting = configNode.getNode("tablist").getNode("sorting");
    }

    private void load() {
        try {
            configNode = configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
