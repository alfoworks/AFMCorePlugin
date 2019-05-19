package ru.allformine.afmcp.commands;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;

import java.util.Map;

public class VipCommand extends AFMCPCommand {
    private ConfigurationNode configNode = AFMCorePlugin.getConfig();
    private Map<Object, ? extends ConfigurationNode> vips = configNode.getNode("vips").getChildrenMap();

    public CommandResult execute(CommandSource source, CommandContext args) {
        String vipToBuy = args.<String>getOne(Text.of("selectedVip")).get();

        if (vipToBuy.equalsIgnoreCase("list")) {
            reply(source, "Доступно к покупке:");

            for (Map.Entry<Object, ? extends ConfigurationNode> entry : vips.entrySet()) {
                ConfigurationNode value = entry.getValue();
                int cost = value.getNode("cost").getInt();

                String fullName = value.getNode("fullName").getString();

                reply(source, String.format("%s%s %s- %s%s", getColor(), fullName, TextColors.WHITE, getColor(), cost));
            }

            reply(source, "Для покупки любой из этих привилегий, напишите /vip <имя>.");

            return CommandResult.success();
        }

        if (source instanceof Player) {
            ConfigurationNode vipNode = configNode.getNode("vips", vipToBuy.toLowerCase());
            if (vipNode != null && !vipNode.isVirtual()) {
                // int cost = vipNode.getNode("cost").getInt(); Для дальнейшей покупки
                String fullName = vipNode.getNode("fullName").getString();

                // TODO: покупка привелегии

                reply(source, "Вы успешно купили привилегию "+fullName+", спасибо!");
            } else {
                reply(source, "Данная привилегия не найдена, введите команду /vip list для отображения списка привилегий.");
            }
            return CommandResult.success();
        } else {
            reply(source, "Данную команду может выполнить только игрок.");
        }

        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "AFMEco";
    }

    @Override
    public TextColor getColor() {
        return TextColors.LIGHT_PURPLE;
    }
}
