package ru.allformine.afmcp.hadkers;

import ru.allformine.afmcp.commands.AFMCPCommand;
import java.util.HashMap;

public class CommandHandler {
    public static HashMap<String, AFMCPCommand> commands = new HashMap<>();

    public static void addCommand(AFMCPCommand command) {
        commands.put(command.getName(), command);
    }
}
