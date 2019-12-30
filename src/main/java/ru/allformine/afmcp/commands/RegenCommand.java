package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class RegenCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource scr, CommandContext args) {
        if (!(scr instanceof Player)) {
            reply(scr, Text.of("Эта команда не может быть выполнена из консоли"));
            return CommandResult.success();
        }

        // Заброшено
        /*
        Player player = (Player) scr;
        player.getWorld().getChunk().get().del

        */
        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "Regen";
    }

    @Override
    public TextColor getColor() {
        return TextColors.RED;
    }
}
