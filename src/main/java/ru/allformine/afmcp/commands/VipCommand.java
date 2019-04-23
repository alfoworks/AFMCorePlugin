package ru.allformine.afmcp.commands;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import ru.allformine.afmcp.AFMCorePlugin;

import java.util.Map;

public class VipCommand implements CommandExecutor {
    private ConfigurationNode configNode = AFMCorePlugin.getConfig();
    private Map<Object, ? extends ConfigurationNode> vips = configNode.getChildrenMap();
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException{
        return CommandResult.success();
    }
}
