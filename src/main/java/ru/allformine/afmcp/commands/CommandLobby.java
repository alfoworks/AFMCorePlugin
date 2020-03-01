package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;

public class CommandLobby extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Эта команда может вызываться только игроком!"));
        }

        String subCommand = args.<String>getOne("subcommand").orElse(null);

        if (subCommand == null) {
            src.sendMessage(Text.of("Список подкоманд:"));

            src.sendMessage(Text.of(String.format("%s%s %s- %s%s", "", "setspawn", "", "", "установить место спавна")));
            src.sendMessage(Text.of(String.format("%s%s %s- %s%s", "", "exit", "", "", "выйти из лобби")));
            src.sendMessage(Text.of(String.format("%s%s %s- %s%s", "", "join", "", "", "войти в лобби")));

            return CommandResult.success();
        }

        if (subCommand.equalsIgnoreCase("setspawn")) {
            // TODO: Доделать
            // PluginConfig.lobbySpawnLocation = LocationUtil.toString(((Player) src).getLocation(),
             //       ((Player) src).getWorld());
            //AFMCorePlugin.

            replyString(src, "Позиция спавна успешно установлена.");
        } else if (subCommand.equalsIgnoreCase("exit")) {
            if (AFMCorePlugin.lobbyCommon.removePlayerFromLobby((Player) src)) {
                replyString(src, "Вы больше не в лобби.");
            } else {
                throw new CommandException(Text.of("Вы не находитесь в лобби."));
            }
        } else if (subCommand.equalsIgnoreCase("join")) {
            if (!AFMCorePlugin.lobbyCommon.addPlayerToLobby((Player) src)) {
                throw new CommandException(Text.of("Вы уже находитесь в лобби!"));
            }
        } else {
            throw new CommandException(Text.of("Неизвестная подкоманда!"));
        }

        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "Lobby";
    }

    @Override
    public TextColor getColor() {
        return TextColors.DARK_GREEN;
    }
}