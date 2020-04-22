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

public class QuestGUICommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Эта команда может вызываться только игроком!"));
        }

        AFMCorePlugin.questDataManager.openGUI((Player) src);
        return CommandResult.success();
    }

    public String getName() {
        return "questgui";
    }

    public TextColor getColor() {
        return TextColors.GREEN;
    }

    void reply(CommandSource source, Text text) {
        source.sendMessage(Text.builder(getName()).color(getColor()).append(Text.builder(" > ").color(TextColors.WHITE).append(text).build()).build());
    }

    void replyString(CommandSource source, String string){
        reply(source, Text.of(string));
    }
}
