package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class AFMCPCommand implements CommandExecutor {
    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }

    public String getName() {
        return "";
    }

    public TextColor getColor() {
        return TextColors.BLACK;
    }

    void reply(CommandSource source, Text text) {
        source.sendMessage(Text.builder(getName()).color(getColor()).append(Text.builder(" > ").color(TextColors.WHITE).append(text).build()).build());
    }

    void replyString(CommandSource source, String string){
        reply(source, Text.of(string));
    }
}
