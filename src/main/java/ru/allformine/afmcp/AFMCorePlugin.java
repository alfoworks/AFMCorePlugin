package ru.allformine.afmcp;

import com.google.inject.Inject;
import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.commands.*;
import ru.allformine.afmcp.jumppad.JumpPadEventListener;
import ru.allformine.afmcp.listeners.*;
import ru.allformine.afmcp.lobby.LobbyCommon;
import ru.allformine.afmcp.lobby.LobbySOI;
import ru.allformine.afmcp.lobby.LobbyVanilla;
import ru.allformine.afmcp.net.api.Broadcast;
import ru.allformine.afmcp.net.api.Webhook;
import ru.allformine.afmcp.quests.*;
import ru.allformine.afmcp.tablist.UpdateTask;
import sun.security.ssl.Debug;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "afmcp",
        name = "AFMCorePlugin",
        description = "A plugin for a couple of random tasks.",
        version = "0.7",
        url = "http://allformine.ru",
        authors = {
                "Iterator, HeroBrine1st_Erq, ReDestroyDeR"
        },
        dependencies = {@Dependency(id = "spotlin", version = "0.2.0")}
)
public class AFMCorePlugin {
    public static boolean serverRestart = false;
    @Inject
    public static Logger logger;
    public static LobbyCommon[] lobbies = new LobbyCommon[]{new LobbySOI(), new LobbyVanilla()};
    public static LobbyCommon currentLobby;
    public static boolean debugSwitch = false;
    public static AFMCorePlugin instance;
    public static Task lagTask;
    public static QuestDataManager questDataManager;
    private static CommentedConfigurationNode configNode;
    private static ConfigurationLoader<CommentedConfigurationNode> configLoader;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;
    private Path configFile;
    private Path questsFile;
    private Path factionListFile;

    public final static String[] defaultFactions = {
            "safezone",
            "warzone"
    };

    public static boolean questToggle;

    public static CommentedConfigurationNode getConfig() {
        return configNode;
    }

    public static void saveConfig() {
        try {
            configLoader.save(configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Inject
    private void setLogger(Logger logger) {
        AFMCorePlugin.logger = logger;
    }

    @Listener
    public void ats(GameAboutToStartServerEvent event) {
        CommentedConfigurationNode quests = getConfig().getNode("quests");
        String questsFilePath = quests.getNode("questsFile").getString();
        assert questsFilePath != null;

        factionListFile = configDir.resolve("factionList.json");
        if (!Files.exists(factionListFile)) {
            try {
                Files.createFile(Paths.get(configDir.toString() + "/factionList.json"));
                logger.debug("Created faction list json file");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        try {
            String pathA = configDir.toString() + "/questFiles";
            if (!Files.exists(Paths.get(pathA))) {
                Files.createDirectory(Paths.get(pathA));
                logger.debug("Created quest files directory");
            }
            if (!questsFilePath.equals("")) {
                questsFile = configDir.resolve("questFiles").resolve(questsFilePath);
                if (!Files.exists(questsFile)) {
                    logger.error("Quests file is set in config, but file is not present in directory");
                    logger.error("Creating new empty quests file with the same name - " + questsFilePath);
                    Files.createFile(Paths.get(pathA + "/" + questsFilePath));
                    questsFile = configDir.resolve("questFiles").resolve(questsFilePath);
                    questToggleOff();
                    logger.debug("Created quests file json file with name " + questsFilePath);
                } else {
                    if (EagleFactionsPlugin.getPlugin().getConfiguration().getPowerConfig().getKillAward() == 0
                    && EagleFactionsPlugin.getPlugin().getConfiguration().getPowerConfig().getPenalty() == 0
                    && EagleFactionsPlugin.getPlugin().getConfiguration().getPowerConfig().getPowerDecrement() == 0
                    && EagleFactionsPlugin.getPlugin().getConfiguration().getPowerConfig().getPowerIncrement() == 0) {
                        questToggleOn();
                    } else {
                        logger.error("To work with Quests system you need to set following settings into Eagle Factions config:\n" +
                                "power {\n" +
                                "    # How much power will be removed on player death. Default: 2.0\n" +
                                "    decrement=0\n" +
                                "    # How much power will be restored for player after 1 minute of playing. (0.04 per minute = 1,2 per hour.) Default: 0.04\n" +
                                "    increment=0\n" +
                                "    # Player kill award. Default: 2.0\n" +
                                "    kill-award=0\n" +
                                "    # Maximum amount of power a player can have. Default: 10.0\n" +
                                "    max-power=[ANY]\n" +
                                "    # Penalty after killing a teammate. Default: 1.0\n" +
                                "    penalty=0\n" +
                                "    # Starting amount of power. Default: 5.0\n" +
                                "    start-power=[ANY]\n" +
                                "}");
                        questToggleOff();
                    }
                }
            } else {
                logger.warn("Quest file isn't present in config. Quest System is disabled");
                questToggleOff();
            }
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
        Sponge.getEventManager().registerListeners(this, new TestEventListener());
        Sponge.getEventManager().registerListeners(this, new MOTDEventListener());
        Sponge.getEventManager().registerListeners(this, new JoinQuitMessageListener());
        Sponge.getEventManager().registerListeners(this, new QuestEventListener());

        if (Sponge.getPluginManager().isLoaded("eaglefactions")) {
            Sponge.getEventManager().registerListeners(this, new FactionEventListener());
        }

        if (Sponge.getPluginManager().isLoaded("ultimatechat")) {
            Sponge.getEventManager().registerListeners(this, new ChatCorrectionListener());
            Sponge.getEventManager().registerListeners(this, new UltimateChatEventListener());
        } else {
            Sponge.getEventManager().registerListeners(this, new DefaultChatEventListener());
        }

        configFile = configDir.resolve("config.conf");
        configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();

        configSetup();

        CommandSpec tablistDebugSpec = CommandSpec.builder()
                .executor(new TabListCommand())
                .build();

        Sponge.getCommandManager().register(this, tablistDebugSpec, "tablist");

        CommandSpec kotlinTestSpec = CommandSpec.builder()
                .executor(new KotlinTestCommand())
                .build();

        Sponge.getCommandManager().register(this, kotlinTestSpec, "kotlintest");

        CommandSpec restartCommandSpec = CommandSpec.builder()
                .description(Text.of("Команда для перезагрузки сервера, единственная верная."))
                .permission("afmcp.admin")
                .arguments(GenericArguments.flags().flag("c").buildWith(GenericArguments.seq(
                        GenericArguments.optional(GenericArguments.integer(Text.of("minutes"))))))
                .executor(new RestartCommand())
                .build();

        Sponge.getCommandManager().register(this, restartCommandSpec, "afmrestart", "servrestart", "restart");

        CommandSpec tokensCommandSpec = CommandSpec.builder()
                .description(Text.of("Команда для получения количества токенов."))
                .executor(new TokensCommand())
                .build();

        Sponge.getCommandManager().register(this, tokensCommandSpec, "tokens");

        CommandSpec questGUICommandSpec = CommandSpec.builder()
                .description(Text.of("Меню квестов."))
                .executor(new QuestGUICommand())
                .build();

        Sponge.getCommandManager().register(this, questGUICommandSpec, "questgui");

        CommandSpec questEDITORCommandSpec = CommandSpec.builder()
                .description(Text.of("Редактор квестов."))
                .executor(new QuestEDITORCommand(configDir, this))
                //.permission("afmcp.admin")
                .build();

        Sponge.getCommandManager().register(this, questEDITORCommandSpec, "questeditor");

        CommandSpec vipCommandSpec = CommandSpec.builder()
                .description(Text.of("Команда для покупки привелегий."))
                .executor(new VipCommand())
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
                .description(Text.of("(MessagingAPI) отправить собщение всем или игроку"))
                .executor(new MessageCommand())
                .arguments(GenericArguments.flags().valueFlag(GenericArguments
                        .player(Text.of("player")), "p").buildWith(GenericArguments.seq(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
                        GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("message"))))))
                .build();

        Sponge.getCommandManager().register(this, messageSpec, "message", "amsg");

        CommandSpec regenSpec = CommandSpec.builder()
                .description(Text.of("Отрегенерировать чанк"))
                .executor(new RegenCommand())
                .build();

        Sponge.getCommandManager().register(this, regenSpec, "regen");

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

        configNode.getNode("webhook", "shutdown").setValue(System.currentTimeMillis() / 1000);
        saveConfig();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        long time = configNode.getNode("webhook", "shutdown").getLong(0);
        if (time == 0) logger.error("No last shutdown time.");
        long loadingTime = System.currentTimeMillis() / 1000 - time;

        if (questToggle)
            cleanQuestFactions();
        Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_STARTED, String.valueOf(loadingTime));

        Task.builder()
                .execute(new UpdateTask())
                .intervalTicks(20)
                .name("TabList Update Task")
                .submit(this);

        if (PluginConfig.broadcastEnabled)
            Task.builder()
                    .execute(new BroadcastTask(Broadcast.getBroadcasts()))
                    .interval(2, TimeUnit.MINUTES)
                    .name("Broadcast Task (AFMCP)")
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

    public static void cleanQuestFactions() {
        Map<String, Faction> map = EagleFactionsPlugin.getPlugin().getFactionLogic().getFactions();
        QuestFactionContainer container = questDataManager.getQuestFactions();

        // Constructing QuestFaction from Faction data
        for (Map.Entry<String, Faction> e : map.entrySet()) {
            if (e.getKey().equalsIgnoreCase("WarZone") || e.getKey().equalsIgnoreCase("SafeZone")) {
                continue;
            }

            // Faction
            if (!container.getQuestFaction(e.getKey()).isPresent()) {
                UUID leader = e.getValue().getLeader();
                Set<UUID> officers = e.getValue().getOfficers();
                Set<UUID> recruits = e.getValue().getRecruits();

                questDataManager.updateContribution(new PlayerContribution(leader, e.getValue()));

                // Adding members
                for (UUID u: officers) {
                    questDataManager.updateContribution(new PlayerContribution(u, e.getValue()));
                }
                for (UUID u: recruits) {
                    questDataManager.updateContribution(new PlayerContribution(u, e.getValue()));
                }
            }
        }
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

        PluginConfig.lobbyEnabled = configNode.getNode("lobby").getNode("enabled").getBoolean(false);
        PluginConfig.lobbyId = configNode.getNode("lobby").getNode("id").getString("");
        PluginConfig.motdDescription = configNode.getNode("motd").getNode("description").getString("No description set.\nBeu?");
        PluginConfig.serverId = configNode.getNode("broadcast").getNode("serverId").getString("");
        PluginConfig.broadcastEnabled = configNode.getNode("broadcast").getNode("enabled").getBoolean(false);
        PluginConfig.lobbyEnabled = configNode.getNode("lobby").getNode("enabled").getBoolean();
        PluginConfig.lobbyId = configNode.getNode("lobby").getNode("id").getString();
        PluginConfig.motdDescription = configNode.getNode("motd").getNode("description").getString();
        ConfigurationNode tablistOptions = configNode.getNode("tablist");
        PluginConfig.tablistSorting = tablistOptions.getNode("sorting");
        PluginConfig.tabListFooter = tablistOptions.getNode("footer").getString("");
        PluginConfig.tabListHeader = tablistOptions.getNode("header").getString("");
        PluginConfig.tabListCoordinates = tablistOptions.getNode("coordinates").getString("");
        PluginConfig.tabListOnlineCount = tablistOptions.getNode("onlineCount").getString("");

    }

    private void load() {
        try {
            configNode = configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void questToggleOff() {
        questToggle = false;
        questDataManager = null;

        logger.warn("Using vanilla EagleFactions power system");
    }

    private void questToggleOn() {
        questToggle = true;
        questDataManager = new QuestDataManager(questsFile, factionListFile);
        questDataManager.initializeQuestFactionContainer();
        if (!questToggle) {
            questToggleOff();
            return;
        }

        logger.warn("Using quests-AFMCP EagleFactions power system");
    }
}
