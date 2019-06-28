package ru.allformine.afmcp.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;

public class RestartCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource scr, CommandContext args) {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            player.kick(Text.builder("Сервер ушёл на рестарт! Увидимся через минуту <3").color(TextColors.LIGHT_PURPLE).build());
        }

        AFMCorePlugin.serverRestart = true;
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "stop");

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
