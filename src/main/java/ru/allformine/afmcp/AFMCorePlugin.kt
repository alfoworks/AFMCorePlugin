package ru.allformine.afmcp

import com.google.inject.Inject
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GamePreInitializationEvent
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.game.state.GameStoppingServerEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import ru.allformine.afmcp.commands.*
import ru.allformine.afmcp.jumppad.JumpPadEventListener
import ru.allformine.afmcp.listeners.*
import ru.allformine.afmcp.lobby.LobbyCommon
import ru.allformine.afmcp.lobby.LobbySOI
import ru.allformine.afmcp.lobby.LobbyVanilla
import ru.allformine.afmcp.net.api.Webhook
import ru.allformine.afmcp.tablist.UpdateTask
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Plugin(id = "afmcp",
        name = "AFMCorePlugin",
        description = "A plugin for a couple of random tasks.",
        version = "0.7", url = "http://allformine.ru",
        authors = ["Iterator, HeroBrine1st_Erq"],
        dependencies = [Dependency(id = "spotlin", version = "0.2.0")])

class AFMCorePlugin {
    @Inject
    @ConfigDir(sharedRoot = false)
    private val configDir: Path? = null
    private var configFile: Path = configDir!!.resolve("config.conf")

    @Inject
    private fun setLogger(logger: Logger) {
        Companion.logger = logger
    }

    @Listener
    fun init(event: GameInitializationEvent?) {
        PacketChannels.FACTIONS = Sponge.getGame()
                .channelRegistrar
                .createRawChannel(this, "factions")
        PacketChannels.MESSAGING = Sponge.getGame()
                .channelRegistrar
                .createRawChannel(this, "afmmessaging")
    }

    @Listener
    fun preInit(event: GamePreInitializationEvent?) {
        instance = this

        Sponge.getEventManager().registerListeners(this, DiscordWebhookListener())
        Sponge.getEventManager().registerListeners(this, JumpPadEventListener())
        Sponge.getEventManager().registerListeners(this, TestEventListener())
        Sponge.getEventManager().registerListeners(this, ChatEventListener())
        Sponge.getEventManager().registerListeners(this, MOTDEventListener())

        Sponge.getEventManager().registerListeners(this, if (Sponge.getPluginManager().isLoaded("ultimatechat")) UltimateChatEventListener() else DefaultChatEventListener())

        if (Sponge.getPluginManager().isLoaded("eaglefactions")) {
            Sponge.getEventManager().registerListeners(this, FactionEventListener())
        }

        configLoader = HoconConfigurationLoader.builder().setPath(configFile).build()
        configSetup()

        val tablistDebugSpec = CommandSpec.builder()
                .executor(TabListCommand())
                .build()
        Sponge.getCommandManager().register(this, tablistDebugSpec, "tablist")

        val kotlinTestSpec = CommandSpec.builder()
                .executor(KotlinTestCommand())
                .build()
        Sponge.getCommandManager().register(this, kotlinTestSpec, "kotlintest")

        val restartCommandSpec = CommandSpec.builder()
                .description(Text.of("Команда для перезагрузки сервера, единственная верная."))
                .permission("afmcp.admin")
                .arguments(GenericArguments.flags().flag("c").buildWith(GenericArguments.seq(
                        GenericArguments.optional(GenericArguments.integer(Text.of("minutes"))))))
                .executor(RestartCommand())
                .build()
        Sponge.getCommandManager().register(this, restartCommandSpec, "afmrestart", "servrestart", "restart")

        val tokensCommandSpec = CommandSpec.builder()
                .description(Text.of("Команда для получения количества токенов."))
                .executor(TokensCommand())
                .build()
        Sponge.getCommandManager().register(this, tokensCommandSpec, "tokens")

        val vipCommandSpec = CommandSpec.builder()
                .description(Text.of("Команда для покупки привелегий."))
                .executor(VipCommand())
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("selectedVip")))
                )
                .build()
        Sponge.getCommandManager().register(this, vipCommandSpec, "vip")

        val rawBCspec = CommandSpec.builder()
                .description(Text.of("Отправить текст в чат в таком виде, в каком вы его пишете."))
                .executor(RawBCCommand())
                .arguments(
                        GenericArguments.remainingJoinedStrings(Text.of("text"))
                )
                .build()
        Sponge.getCommandManager().register(this, rawBCspec, "rawbc")

        val debugSpec = CommandSpec.builder()
                .description(Text.of("Debug switch"))
                .executor(DebugCommand())
                .build()
        Sponge.getCommandManager().register(this, debugSpec, "debugswitch")

        val lobbySpec = CommandSpec.builder()
                .description(Text.of("Лобби"))
                .executor(LobbyCommand())
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("subcommand"))))
                .build()
        Sponge.getCommandManager().register(this, lobbySpec, "lobby")

        val tickIntervalSpec = CommandSpec.builder()
                .description(Text.of("Fuck"))
                .executor(TickIntervalCommand())
                .arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("interval"))))
                .build()
        Sponge.getCommandManager().register(this, tickIntervalSpec, "tickinterval", "ti")

        val messageSpec = CommandSpec.builder()
                .description(Text.of("(MessagingAPI) отправить собщение всем или игроку"))
                .executor(MessageCommand())
                .arguments(GenericArguments.flags().valueFlag(GenericArguments
                        .player(Text.of("player")), "p").buildWith(GenericArguments.seq(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
                        GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("message"))))))
                .build()
        Sponge.getCommandManager().register(this, messageSpec, "message", "amsg")

        if (PluginConfig.lobbyId != null) {
            for (lobby in lobbies) {
                if (lobby.lobbyId == PluginConfig.lobbyId) {
                    Sponge.getEventManager().registerListeners(this, lobby)
                    currentLobby = lobby
                }
            }
        }
    }

    @Listener
    fun onServerStop(event: GameStoppingServerEvent?) {
        if (serverRestart) {
            Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_RESTARTING)
        } else {
            Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_STOPPED)
        }
    }

    @Listener
    fun onServerStart(event: GameStartedServerEvent?) {
        Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_STARTED)
        Task.builder()
                .execute(UpdateTask())
                .intervalTicks(20)
                .name("TabList Update Task")
                .submit(this)

        if (!PluginConfig.lobbyEnabled) {
            return
        }

        if (PluginConfig.lobbyId == "") {
            logger!!.error("Lobby id is empty!")
            PluginConfig.lobbyId = null
        }

        PluginConfig.lobbySpawn = LocationSerializer().deserialize(config!!.getNode("lobby").getNode("location"))
    }

    private fun configSetup() {
        if (!Files.exists(configDir!!)) {
            try {
                Files.createDirectories(configDir)
            } catch (io: IOException) {
                io.printStackTrace()
            }
        }

        if (!Files.exists(configFile)) {
            try {
                Sponge.getAssetManager().getAsset(this, "config.conf").get().copyToFile(configFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        load()

        PluginConfig.lobbyEnabled = config!!.getNode("lobby").getNode("enabled").boolean
        PluginConfig.lobbyId = config!!.getNode("lobby").getNode("id").string
        PluginConfig.motdDescription = config!!.getNode("motd").getNode("description").string
        PluginConfig.tablistSorting = config!!.getNode("tablist").getNode("sorting")
    }

    private fun load() {
        try {
            config = configLoader!!.load()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmField
        var serverRestart = false

        @JvmField
        @Inject
        var logger: Logger? = null
        var config: CommentedConfigurationNode? = null
            private set
        var lobbies = arrayOf(LobbySOI(), LobbyVanilla())

        @JvmField
        var currentLobby: LobbyCommon? = null

        @JvmField
        var debugSwitch = false

        @JvmField
        var instance: AFMCorePlugin? = null
        @JvmField

        var lagTask: Task? = null
        private var configLoader: ConfigurationLoader<CommentedConfigurationNode>? = null

        @JvmStatic
        fun saveConfig() {
            try {
                configLoader!!.save(config!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}