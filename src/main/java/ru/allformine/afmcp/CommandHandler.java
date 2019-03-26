package ru.allformine.afmcp;

import ru.allformine.afmcp.commands.AFMCMCommand;
import java.util.HashMap;

public class CommandHandler {
    static HashMap<String, AFMCMCommand> commands = new HashMap<>();

    public static void addConnabd(AFMCMCommand AFMCMCommand) {
        commands.put(AFMCMCommand.name, AFMCMCommand);
    }
}
