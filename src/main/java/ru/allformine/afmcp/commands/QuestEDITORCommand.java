package ru.allformine.afmcp.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.quests.QuestEditor;

import java.nio.file.Path;

public class QuestEDITORCommand extends AFMCPCommand {

    private final Path configDir;
    private final AFMCorePlugin afmcp;

    public QuestEDITORCommand(Path configDir, AFMCorePlugin afmcp) {
        this.configDir = configDir;
        this.afmcp = afmcp;
    }
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Эта команда может вызываться только игроком!"));
        }
        if (!AFMCorePlugin.questToggle) {

            reply(src, Text.of(TextColors.RED, "ВНИМАНИЕ!!! НЕЛЬЗЯ ВЫХОДИТЬ ВО ВРЕМЯ РАБОТЫ С ЭТОЙ КОМАНДОЙ, ИНАЧЕ ПИЗДА СЕРВЕРУ И НУЖНО ВСЕ ПЕРЕЗАПУСКАТЬ БУДЕТ"));
            reply(src, Text.of(TextColors.RED, "Сейчас всех, кроме вас, кикнет с сервера и на время работы команды никто не сможет войти на сервер."));
            for (Object o : Sponge.getServer().getOnlinePlayers().toArray()) {
                if (o instanceof Player) {
                    Player p = (Player) o;
                    if (p.getUniqueId() != ((Player) src).getUniqueId()) {
                        p.kick(Text.of(TextColors.RED, "Технические работы по редактированию квестов!\nИзвините за неудобства"));
                    }
                }
            }
            reply(src, Text.of(TextColors.YELLOW, "Creating QuestEditor"));
            try {
                QuestEditor questEditor = new QuestEditor(src, configDir, afmcp);
            } catch (Exception e) {
                reply(src, Text.of(TextColors.RED, "ПРОИЗОШЛА ОШИБКА!"));
                reply(src, Text.of(TextColors.RED, e.getStackTrace()));
            }
        } else {
            reply(src, Text.of(TextColors.RED, "Отключите систему квестов для того, что бы начать редактирование"));
        }
        return CommandResult.success();
    }

    public String getName() {
        return "Quest Editor";
    }

    public TextColor getColor() {
        return TextColors.AQUA;
    }

    void reply(CommandSource source, Text text) {
        source.sendMessage(Text.builder(" [").color(TextColors.GRAY).append(
                Text.builder(getName()).color(getColor()).append(
                        Text.builder("] ").color(TextColors.GRAY).append(text).build()).build()).build());
    }

    void replyString(CommandSource source, String string){
        reply(source, Text.of(string));
    }
}
