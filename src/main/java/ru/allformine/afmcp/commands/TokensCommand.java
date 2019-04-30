package ru.allformine.afmcp.commands;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.http.GETResponse;
import ru.allformine.afmcp.net.http.Requests;

public class TokensCommand implements CommandExecutor {
    private ConfigurationNode configNode = AFMCorePlugin.getConfig().getNode("eco");
    private String key = configNode.getNode("key").getString();
    private String balanceUrl = configNode.getNode("balanceUrl").getString();
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (source instanceof Player) {
            String url = balanceUrl + "&act=get&key=" + key + "&nickname=" + source.getName(); // есть причины. что бы это не заработало: это слишком просто
            GETResponse response = Requests.sendGet(url);

            if (response != null && response.responseCode == 200) {
                String responseToPlayer = "Ваш баланс: " + response.response + " токенов.";
                source.sendMessage(Text.of(responseToPlayer));
                return CommandResult.success();
            } else {
                source.sendMessage(Text.of("Произошла серверная ошибка при выполнении команды."));
                throw new CommandException(Text.of(response.toString()));
            }
        } else {
            source.sendMessage(Text.of("Вы не являетесь игроком"));
            return CommandResult.success();
        }
    }
}
