package ru.allformine.afmcp.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.net.discord.Discord;

public class RestartCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource scr, CommandContext args) throws CommandException {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            player.kick(Text.of(TextColors.LIGHT_PURPLE+"Сервер ушёл на рестарт! Увидимся через минуту <3"));
        }

        Discord.serverRestart = true;
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "stop");

        reply(scr, "Сервер перезагружается!");

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
