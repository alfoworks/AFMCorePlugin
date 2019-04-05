package ru.allformine.afmcp;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;
import ru.allformine.afmcp.commands.*;
import ru.allformine.afmcp.hadlers.CommandHandler;
import ru.allformine.afmcp.hadlers.EventListener;
import ru.allformine.afmcp.hadlers.ProtocolHandler;
import ru.allformine.afmcp.net.discord.Discord;
import ru.allformine.afmcp.net.http.HTTPServer;

import java.util.ArrayList;
import java.util.Arrays;

public class AFMCorePlugin extends JavaPlugin implements PluginMessageListener {
    private HTTPServer apiServer = new HTTPServer();

    private static byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("AFMCorePlugin");
    }

    public void onEnable() {
        EventListener listener = new EventListener();
        Bukkit.getPluginManager().registerEvents(listener, this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "Notify");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "C234Fb");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "C234Fb", this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "ambient");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "territoryshow");

        this.saveDefaultConfig();

        Bukkit.getServer().getScheduler().runTaskAsynchronously(this, apiServer);

        try {
            //noinspection deprecation
            ProtocolHandler.vanishManager = VanishNoPacket.getManager();
        } catch (VanishNotLoadedException ex) {
            System.out.println("Can't find VanishNoPacket.");
        }

        ProtocolHandler.startHandler();

        CommandHandler.addCommand(new CommandAFMCP());
        CommandHandler.addCommand(new CommandAFMCPLog());
        CommandHandler.addCommand(new CommandFreeze());
        CommandHandler.addCommand(new CommandMaintenance());
        CommandHandler.addCommand(new CommandNotify());
        CommandHandler.addCommand(new CommandPluginReload());
        CommandHandler.addCommand(new CommandRawBC());
        CommandHandler.addCommand(new CommandRGParam());
        CommandHandler.addCommand(new CommandTokens());
        CommandHandler.addCommand(new CommandVIP());

        Discord.sendMessageServer(Discord.MessageTypeServer.TYPE_SERVER_STARTED);
    }

    public void onDisable() {
        apiServer.stop();

        Discord.sendMessageServer(Discord.MessageTypeServer.TYPE_SERVER_STOPPED);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("C234Fb")) {
            if (apiServer.playerScreenshotData.get(player) != null) {
                message = trim(message);
                message = Arrays.copyOf(message, message.length - 1);

                byte[] prevArr = apiServer.playerScreenshotData.get(player);
                apiServer.playerScreenshotData.put(player, ArrayUtils.addAll(prevArr, message));

                if (message.length < 10240) {
                    apiServer.playerScreenshotConfirmation.put(player, true);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        AFMCPCommand command = CommandHandler.commands.get(cmd.getName().toLowerCase());

        if(command != null) {
            if ((command.isPlayerOnly() && sender instanceof Player) || !command.isPlayerOnly()) {
                ArrayList<String> args_list = new ArrayList<>(Arrays.asList(args));

                try {
                    return command.run(args_list, sender);
                } catch(Exception e) {
                    sender.sendMessage(ChatColor.RED+command.getDisplayName()+" > Произошла ошибка при выполнении команды!");
                    sender.sendMessage(ChatColor.RED+e.toString());

                    e.printStackTrace();

                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED+command.getDisplayName()+" > Данная команда не может быть выполнена из консоли!!");

                return true;
            }
        }

        return false;
    }
}
