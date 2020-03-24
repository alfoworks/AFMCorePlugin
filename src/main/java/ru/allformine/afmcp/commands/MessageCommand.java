package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.Messaging;

import java.util.Arrays;

public class MessageCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource scr, CommandContext args) throws CommandException {
        if (!args.<String>getOne("type").isPresent() || !args.<String>getOne("message").isPresent()) {
            throw new CommandException(Text.of("Недостаточно аргументов!"));
        }

        String message = args.<String>getOne("message").get();
        Messaging.MessageType type;

        try {
            type = Messaging.MessageType.valueOf(args.<String>getOne("type").get().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            reply(scr, Text.of("Неверный тип сообщения! Правильные типы:"));

            for (String messageType : Arrays.stream(Messaging.MessageType.values()).map(Enum::name).toArray(String[]::new)) {
                scr.sendMessage(Text.of(messageType));
            }

            return CommandResult.success();
        }

        Messaging.sendMessage(null, message, type);

        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "Messaging";
    }

    @Override
    public TextColor getColor() {
        return TextColors.DARK_BLUE;
    }
}
