package ru.allformine.afmcp.commands;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.api.Eco;

import java.util.Map;
import java.util.OptionalInt;

public class VipCommand extends AFMCPCommand {
    private ConfigurationNode configNode = AFMCorePlugin.Companion.getConfig();
    private Map<Object, ? extends ConfigurationNode> vips = configNode.getNode("vips").getChildrenMap();

    public CommandResult execute(CommandSource source, CommandContext args) {
        String vipToBuy = args.<String>getOne("selectedVip").orElse("list");

        if (vipToBuy.equalsIgnoreCase("list")) {
            reply(source, Text.of("Список привилегий:"));

            for (Map.Entry<Object, ? extends ConfigurationNode> entry : vips.entrySet()) {
                ConfigurationNode value = entry.getValue();
                String cost = String.valueOf(value.getNode("cost").getInt());

                String fullName = value.getNode("fullName").getString();

                source.sendMessage(TextTemplate.of("    ", getColor(), fullName, TextColors.WHITE, " - ", getColor(), cost, " токенов").toText());
            }

            reply(source, Text.of("Для покупки любой из этих привилегий, напишите /vip <имя>."));

            return CommandResult.success();
        }

        if (source instanceof Player) {
            ConfigurationNode vipNode = configNode.getNode("vips", vipToBuy.toLowerCase());
            if (!vipNode.isVirtual()) {
                int cost = vipNode.getNode("cost").getInt(); // Для дальнейшей покупки
                Eco eco = new Eco(source.getName());
                OptionalInt balance = eco.getBalance();
                if (balance.isPresent()) {
                    if (balance.getAsInt() < cost) {
                        reply(source, Text.of("Недостаточно токенов для покупки."));
                        return CommandResult.success();
                    }
                } else {
                    reply(source, Text.of("Произошла неизвестная ошибка."));
                    System.out.println("Can't get balance for player " + source.getName());
                    return CommandResult.success();
                }
                boolean success = eco.decrease(cost);
                if (!success) {
                    reply(source, Text.of("Произошла неизвестная ошибка."));
                    System.out.println("Can't decrease balance for player " + source.getName());
                    return CommandResult.success();
                }
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
                        String.format("setvip %s %s %s",
                                source.getName(),
                                vipToBuy.toLowerCase(),
                                vipNode.getNode("period").getInt()
                        )
                );

                reply(source, Text.of("Привилегия успешно приобретена."));
            } else {
                reply(source, Text.of("Данная привилегия не найдена, введите команду /vip list для списка привилегий."));
            }
            return CommandResult.success();
        } else {
            reply(source, Text.of("Данную команду может выполнить только игрок."));
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
