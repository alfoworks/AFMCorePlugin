package ru.allformine.afmcp;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;
import ru.allformine.afmcp.commands.AFMCPCommand;
import ru.allformine.afmcp.commands.CommandAFMCP;
import ru.allformine.afmcp.net.discord.Discord;
import ru.allformine.afmcp.net.http.HTTPServer;
import java.util.ArrayList;
import java.util.Arrays;

public class AFMCorePlugin extends JavaPlugin implements PluginMessageListener {
    private HTTPServer apiServer = new HTTPServer();
    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("AFMCorePlugin");
    }

    public void onEnable() {
        new EventListener(this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "Notify");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "C234Fb");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "C234Fb", this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "ambient");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "territoryshow");

        this.saveDefaultConfig();

        Bukkit.getServer().getScheduler().runTaskAsynchronously(this, apiServer);

        try { //Проверялка на то, есть ли плагин на ваниш.
            //noinspection deprecation
            ProtocolHandler.vanishManager = VanishNoPacket.getManager();
        } catch (VanishNotLoadedException ex) {
            System.out.println("Can't find VanishNoPacket.");
        }

        ProtocolHandler.startHandler();

        CommandHandler.addCommand(new CommandAFMCP());

        Discord.sendMessage("Сервер поднялся!", false, "TechInfo", 1); //отправляем в дс сообщеньку, что сервак врублен.
    }

    //Сообщение в дискорд о том, что сервер упал.
    public void onDisable() {
        apiServer.cancel();

        Discord.sendMessageSync("@everyone Сервер упал!", false, "TechInfo", 1);
    }

    //Скриншотер
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("C234Fb")) {
            if (apiServer.playerScreenshotData.get(player) != null) {
                message = Util.trim(message);
                message = Arrays.copyOf(message, message.length - 1);

                byte[] prevArr = apiServer.playerScreenshotData.get(player);
                apiServer.playerScreenshotData.put(player, ArrayUtils.addAll(prevArr, message));

                if (message.length < 10240) {
                    apiServer.playerScreenshotConfirmation.put(player, true);
                }
            }
        }
    }

    //Ебанные команды
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        AFMCPCommand command = CommandHandler.commands.get(cmd.getName());

        if(command != null) {
            ArrayList<String> args_list = new ArrayList<>(Arrays.asList(args));

            return command.run(args_list, sender);
        }

        return false;
    }
}
