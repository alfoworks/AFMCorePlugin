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
import ru.allformine.afmcp.net.http.GETResponse;
import ru.allformine.afmcp.net.http.Requests;

public class TokensCommand extends AFMCPCommand {
    private ConfigurationNode configNode = AFMCorePlugin.getConfig().getNode("eco");
    private String key = configNode.getNode("key").getString();
    private String balanceUrl = configNode.getNode("balanceUrl").getString();

    public CommandResult execute(CommandSource source, CommandContext args) {
        if (source instanceof Player) {
            String url = balanceUrl + "&act=get&key=" + key + "&nick=" + source.getName();
            GETResponse response = Requests.sendGet(url);

            if (response != null && response.responseCode == 200) {
                reply(source, "Ваш баланс: " + Text.builder(response.response).color(getColor()) + " токенов.");
            } else {
                reply(source, "Произошла неизвестная ошибка при выполнении команды!");
            }
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
