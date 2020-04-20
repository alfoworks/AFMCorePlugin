package ru.allformine.afmcp.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.api.Eco;

import java.util.Objects;

public class DebugChestCommand extends AFMCPCommand{
    public CommandResult execute(CommandSource source, CommandContext args) {
        Plugin plugin = (Plugin) Objects.requireNonNull(Sponge.getPluginManager().getPlugin("afmcp").orElse(null));
        if (source instanceof Player) {
            Inventory inventory = Inventory.builder().build(plugin);
            //int level = AFMCorePlugin.questDataManager.getContribution(((Player) source).getUniqueId());
            inventory.slots().forEach(x -> x.set(ItemStack.builder().itemType(ItemTypes.BOOK).build()));
            ((Player) source).openInventory(inventory);
        } else {
            reply(source, Text.of("Данную команду может выполнить только игрок."));
        }

        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "debugchest";
    }

    @Override
    public TextColor getColor() {
        return TextColors.RED;
    }
}
