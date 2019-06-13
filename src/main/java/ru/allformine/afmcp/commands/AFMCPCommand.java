package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class AFMCPCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource scr, CommandContext args) {
        return CommandResult.success();
    }

    public String getName() {
        return "";
    }

    public TextColor getColor() {
        return TextColors.BLACK;
    }

    void reply(CommandSource source, Text text) {
        source.sendMessage(TextTemplate.of("", getColor(), getName(), TextColors.WHITE, " > ", text).toText());
    }
}
