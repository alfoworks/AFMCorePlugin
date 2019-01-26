package ru.allformine.afmcp;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.command.CraftConsoleCommandSender;

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
