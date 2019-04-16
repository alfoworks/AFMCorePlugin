package ru.allformine.afmcp.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import ru.allformine.afmcp.net.discord.Discord;

public class RestartCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource scr, CommandContext args) throws CommandException {
        Discord.serverRestart = true;

        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "stop");

        return CommandResult.success();
    }
}
