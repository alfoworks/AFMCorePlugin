package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.Messaging;

import java.util.Arrays;

public class MessagePlayerCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource scr, CommandContext args) throws CommandException {
        if (!args.<Player>getOne("player").isPresent() || !args.<String>getOne("type").isPresent() || !args.<String>getOne("message").isPresent()) {
            throw new CommandException(Text.of("Недостаточно аргументов!"));
        }

        String message = args.<String>getOne("message").get();
        Player player = args.<Player>getOne("player").get();
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

        Messaging.sendMessage(player, message, type);

        reply(scr, Text.of(String.format("Сообщение было отправлено игроку %s.", player.getName())));

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
