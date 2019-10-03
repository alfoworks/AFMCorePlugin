package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.vanish.VanishManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VanishCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Optional<String> subCommand = args.getOne(Text.of("subcmd"));

        if (subCommand.isPresent() && subCommand.get().equals("list")) {
            List<String> vanished = VanishManager.vanishedPlayers.stream().map(Player::getName).collect(Collectors.toList());

            if (vanished.size() < 1) {
                reply(src, Text.of("В данный момент на сервере нет игроков в ванише."));

                return CommandResult.success();
            }

            reply(src, Text.of(String.format("Список игроков в ванише: %s.", String.join(",", vanished))));

            return CommandResult.success();
        } else if (subCommand.isPresent()) {
            reply(src, Text.of("Неизвестная подкоманда."));

            return CommandResult.success();
        }

        // ======================================================== //

        if (!(src instanceof Player)) {
            reply(src, Text.of("Вы не можете выполнить это из консоли."));

            return CommandResult.success();
        }

        Player player = (Player) src;

        VanishManager.switchVanish(player);
        reply(src, Text.of(VanishManager.isVanished(player) ? "Теперь вы в ванише." : "Вы выключили ваниш."));

        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "Vanish";
    }

    @Override
    public TextColor getColor() {
        return TextColors.DARK_AQUA;
    }
}
