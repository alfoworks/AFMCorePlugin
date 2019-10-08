package ru.allformine.afmcp.vanish;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VanishManager {
    public static final String vanishPermission = "afmcp.vanish.staff";

    public static List<Player> vanishedPlayers = new ArrayList<>();

    public static boolean isVanished(Player player) {
        return vanishedPlayers.contains(player);
    }

    public static void vanishPlayer(Player player, boolean onJoin) {
        setVanish(player, true, onJoin);

        vanishedPlayers.add(player);

        vanishNotify(String.format(onJoin ? "%s скрытно вошёл в игру" : "%s вошел в ваниш", player.getName()));
    }

    public static void unvanishPlayer(Player player, boolean onLeave) {
        setVanish(player, false, onLeave);

        vanishedPlayers.remove(player);

        vanishNotify(String.format(onLeave ? "%s вышел из игры (персонал)" : "%s вышел из ваниша", player.getName()));
    }

    public static void switchVanish(Player player) {
        if (isVanished(player)) {
            unvanishPlayer(player, false);
        } else {
            vanishPlayer(player, false);
        }
    }

    public static int getPlayerCountExcludingVanished() { // Для AFMUF.
        int count = 0;

        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (!isVanished(player)) {
                count++;
            }
        }

        return count;
    }

    // =============================== //

    private static void setVanish(Player player, boolean enable, boolean silent) {
        player.offer(Keys.INVISIBLE, enable);
        player.offer(Keys.VANISH, enable);
        player.offer(Keys.VANISH_IGNORES_COLLISION, enable);
        player.offer(Keys.VANISH_PREVENTS_TARGETING, enable);

        if (!silent) VanishEffects.applyVanishEffect(player);

        onVanishUpdate();
    }

    private static void vanishNotify(String message) {
        MessageChannel.permission(vanishPermission).send(Text.builder().append(Text.of(message)).color(TextColors.DARK_AQUA).build());
    }

    private static void onVanishUpdate() {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            TabList tabList = player.getTabList();

            for (TabListEntry entry : tabList.getEntries()) {
                Optional<Text> nick = entry.getDisplayName();

                tabList.removeEntry(entry.getProfile().getUniqueId());
            }
        }
    }
}