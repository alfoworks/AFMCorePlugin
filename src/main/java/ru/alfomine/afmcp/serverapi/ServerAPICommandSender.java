package ru.alfomine.afmcp.serverapi;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.command.CraftConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

public class ServerAPICommandSender extends CraftConsoleCommandSender {
    private List<String> outputList = new ArrayList<>();

    @Override
    public void sendRawMessage(String message) {
        outputList.add(ChatColor.stripColor(message));
    }

    @Override
    public String getName() {
        return "ServerAPI";
    }

    public List<String> getOutput() {
        return outputList;
    }
}
