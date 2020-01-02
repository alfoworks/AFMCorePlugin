package ru.alfomine.afmcp.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.LocationUtil;
import ru.alfomine.afmcp.PluginStatics;

public class MainEventListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if ((PluginStatics.playerChestPreset.containsKey(event.getPlayer()) || PluginStatics.playerChestSet.containsKey(event.getPlayer())) && !event.getClickedBlock().getType().equals(Material.CHEST)) {
            event.getPlayer().sendMessage("Вы нажали не на сундук. Введите команду еще раз и попробуйте снова.");

            return;
        }

        if (PluginStatics.playerChestSet.containsKey(event.getPlayer())) {
            if (PluginStatics.playerDel.contains(event.getPlayer())) {
                processUnset(event.getPlayer(), event.getClickedBlock());
            } else {
                processSet(event.getPlayer(), event.getClickedBlock());
            }
        } else if (PluginStatics.playerChestPreset.containsKey(event.getPlayer())) {
            processPreset(event.getPlayer(), event.getClickedBlock());
        } else {
            return;
        }

        AFMCorePlugin.getPlugin().saveConfig();

        event.setCancelled(true);
    }

    // ================================ //
    private static void processPreset(Player player, Block block) {
        FileConfiguration config = AFMCorePlugin.config;
        String name = PluginStatics.playerChestPreset.get(player);

        PluginStatics.playerChestPreset.remove(player);

        if (config.get("presets." + name) != null) {
            player.sendMessage("Шаблон с таким именем уже существует.");

            return;
        }

        if (config.get("chests." + name) == LocationUtil.toString(block.getLocation())) {
            player.sendMessage("Вы не можете сделать этот сундук шаблоном.");

            return;
        }

        config.set("presets." + name, LocationUtil.toString(block.getLocation()));

        player.sendMessage("Вы успешно добавили новый шаблон.");
    }

    private static void processSet(Player player, Block block) {
        FileConfiguration config = AFMCorePlugin.config;
        String name = PluginStatics.playerChestSet.get(player);

        PluginStatics.playerChestSet.remove(player);

        if (config.get("presets." + name) == null) {
            player.sendMessage("Шаблон с таким именем не существует.");

            return;
        }

        if (config.get("presets." + name).equals(LocationUtil.toString(block.getLocation()))) {
            player.sendMessage("Этот сундук установлен как шаблон. Вы не можете установить шаблон на него.");

            return;
        }

        String chestLocation = config.getString("presets." + name);
        Location location = LocationUtil.fromString(chestLocation);

        if (!location.getBlock().getType().equals(Material.CHEST)) {
            player.sendMessage("Не удалось найти сундук на нужном месте. Шаблон удалён.");
            config.set("presets." + name, null);

            return;
        }

        config.set("chests." + name, LocationUtil.toString(block.getLocation()));

        player.sendMessage("Вы успешно установили шаблн сундуку.");
    }

    private static void processUnset(Player player, Block block) {
        FileConfiguration config = AFMCorePlugin.config;

        PluginStatics.playerChestSet.remove(player);
        PluginStatics.playerDel.remove(player);

        for(Object entry : config.getConfigurationSection("chests").getValues(false).values()) {
            String value = (String) entry;

            if (value.equals(LocationUtil.toString(block.getLocation()))) {
                player.sendMessage("Шаблон был удалён с сундука.");

                return;
            }
        }

        player.sendMessage("У этого сундука нет установленного шаблона.");
    }
}
