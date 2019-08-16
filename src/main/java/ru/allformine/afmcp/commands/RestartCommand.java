package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.Utils;

public class RestartCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource scr, CommandContext args) {
        Utils.afmRestart();

        reply(scr, Text.of("Сервер перезапускается!"));
        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "AFMRestart";
    }

    @Override
    public TextColor getColor() {
        return TextColors.RED;
    }
}
