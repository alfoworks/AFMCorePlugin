package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;

public class DebugCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource scr, CommandContext args) {
        AFMCorePlugin.debugSwitch = !AFMCorePlugin.debugSwitch;
        reply(scr, Text.of("Теперь дебаг " + (AFMCorePlugin.debugSwitch ? "включен" : "выключен")));

        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "Debug";
    }

    @Override
    public TextColor getColor() {
        return TextColors.BLUE;
    }
}
