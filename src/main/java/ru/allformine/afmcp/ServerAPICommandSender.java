package ru.allformine.afmcp;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.command.CraftConsoleCommandSender;

public class ServerAPICommandSender extends CraftConsoleCommandSender {
    private String lastOutput = "";

    @Override
    public void sendRawMessage(String message) {
        lastOutput = ChatColor.stripColor(message);
    }

    @Override
    public String getName() {
        return "ServerAPI";
    }

    public String getLastOutput() {
        return lastOutput;
    }
}
