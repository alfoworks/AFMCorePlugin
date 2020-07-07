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
        if (AFMCorePlugin.questToggle) {
            if (AFMCorePlugin.questDataManager.getContribution(((Player) src).getUniqueId()) != null) {
                AFMCorePlugin.questDataManager.openGUI((Player) src, -1);
            } else {
                throw new CommandException(Text.of("Для работы этой команды, необходимо вступить в факцию!"));
            }
            return CommandResult.success();
        }
        reply(src, Text.of(TextColors.RED, "Энергия при помощи квестов не работает! Используйте ванильную имплементацию"));
        return CommandResult.success();
    }

    public String getName() {
        return "Quests";
    }

    public TextColor getColor() {
        return TextColors.GREEN;
    }

    void reply(CommandSource source, Text text) {
        source.sendMessage(Text.builder(" [ ").color(TextColors.GRAY).append(
                Text.builder(getName()).color(getColor()).append(
                        Text.builder(" ] ").color(TextColors.GRAY).append(text).build()).build()).build());
    }

    void replyString(CommandSource source, String string){
        reply(source, Text.of(string));
    }
}
