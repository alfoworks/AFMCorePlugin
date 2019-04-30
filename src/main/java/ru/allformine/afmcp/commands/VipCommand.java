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

import java.util.Map;

public class VipCommand implements CommandExecutor {
    private ConfigurationNode configNode = AFMCorePlugin.getConfig();
    private Map<Object, ? extends ConfigurationNode> vips = configNode.getNode("vips").getChildrenMap();

    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        String vipToBuy = args.<String>getOne(Text.of("selectedVip")).get();

        if (vipToBuy.toLowerCase() == "list") {
            StringBuilder builder = new StringBuilder("Доступно к покупке:\n"); // TODO: дописать строку

            for (Map.Entry<Object, ? extends ConfigurationNode> entry : vips.entrySet()) {
                Object key = entry.getKey();
                ConfigurationNode value = entry.getValue();
                int cost = value.getNode("cost").getInt();
                String fullName = value.getNode("fullName").getString();
                String name = key.toString();
                //builder.append("/vip ").append(name).append(" -> ").append().append(" цена: ").append(cost).append(" токенов.\n");
                builder.append(fullName).append(":").append("Цена: ").append(cost).append(" токенов. Для покупки: /vip ").append(name).append("\n");
            }

            source.sendMessage(Text.of(builder));
            return CommandResult.success();
        }
        if (source instanceof Player) {
            ConfigurationNode vipNode = configNode.getNode("vips", vipToBuy);
            if(vipNode != null && !vipNode.isVirtual()){
                int cost = vipNode.getNode("cost").getInt();
                String fullName = vipNode.getNode("fullName").getString();
                // TODO: покупка привелегии
                source.sendMessage(Text.of("Вы купили привелегию " + fullName + " за " + cost + " токенов."));
                // Sponge.getServer(). странный комментарий, оставлю
            }else{
                source.sendMessage(Text.of("Неверное имя привилегии. Введите /vip для получения всех привелегий"));
            }
            return CommandResult.success();
        } else {
            source.sendMessage(Text.of("Вы не являетесь игроком."));

            throw new CommandException(Text.of("NonePlayerObject"));
        }
    }
}
