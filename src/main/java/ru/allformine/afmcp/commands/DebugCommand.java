package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;

public class DebugCommand extends AFMCPCommand {
    private static Task task;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        AFMCorePlugin.debugSwitch = !AFMCorePlugin.debugSwitch;
        reply(src, Text.of("Теперь дебаг " + (AFMCorePlugin.debugSwitch ? "включен" : "выключен")));

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
