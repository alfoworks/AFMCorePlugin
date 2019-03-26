package ru.allformine.afmcp;

import ru.allformine.afmcp.commands.AFMCPCommand;
import java.util.HashMap;

class CommandHandler {
    static HashMap<String, AFMCPCommand> commands = new HashMap<>();

    static void addCommand(AFMCPCommand command) {
        commands.put(command.name, command);
    }
}
