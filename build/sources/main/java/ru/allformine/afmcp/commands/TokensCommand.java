package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.net.api.Eco;

import java.util.OptionalInt;

public class TokensCommand extends AFMCPCommand {

    public CommandResult execute(CommandSource source, CommandContext args) {
        if (source instanceof Player) {
            Eco eco = new Eco((Player) source);
            OptionalInt balance = eco.getBalance();

            if (balance.isPresent()) {
                reply(source, TextTemplate.of("Ваш баланс: ", getColor(), balance.getAsInt(), " токенов", TextColors.WHITE, ".").toText());
            } else {
                reply(source, Text.of("Произошла неизвестная ошибка."));
            }
        } else {
            reply(source, Text.of("Данную команду может выполнить только игрок."));
        }

        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "AFMEco";
    }

    @Override
    public TextColor getColor() {
        return TextColors.LIGHT_PURPLE;
    }
}
