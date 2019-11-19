package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.vanish.VanishManager;

public class VanishNoInteractCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (src instanceof Player) {
            if (!VanishManager.isVanished((Player) src)) {
                reply(src, Text.of("Для выполнения этой команды вы должны находиться в ванише."));
                return CommandResult.success();
            }

            boolean state = VanishManager.switchCanInteract((Player) src);

            Text.Builder text = Text.builder();
            text.append(Text.of("Теперь вы "));

            if (state) text.append(Text.of("можете"));
            else text.append(Text.builder().append(Text.of("не можете")).color(TextColors.RED).build());

            text.append(Text.of(" взаимодействовать с миром."));

            reply(src, text.build());
        } else {
            reply(src, Text.of("Еблан, от консоли нельзя"));
        }
        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "Vanish";
    }

    @Override
    public TextColor getColor() {
        return TextColors.DARK_AQUA;
    }
}