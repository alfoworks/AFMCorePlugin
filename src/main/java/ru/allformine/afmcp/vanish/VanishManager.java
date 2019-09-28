package ru.allformine.afmcp.vanish;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.HashMap;

public class VanishManager {
    public static final String vanishPermission = "afmcp.vanish.onjoin";
    public static final String notifyPermission = "afmcp.vanish.notify";

    private static HashMap<Player, Boolean> vanishedPlayers = new HashMap<>();

    public static boolean isVanished(Player player) {
        return vanishedPlayers.containsKey(player);
    }

    public static void vanishPlayer(Player player, boolean onJoin) {
        setVanish(player, true, onJoin);

        vanishedPlayers.put(player, true);

        vanishNotify(onJoin ? NotifyMessage.JOIN : NotifyMessage.VANISH_SWITCH_ON, player);
    }

    public static void unvanishPlayer(Player player, boolean onLeave) {
        setVanish(player, false, false);

        vanishedPlayers.remove(player);

        vanishNotify(onLeave ? NotifyMessage.QUIT : NotifyMessage.VANISH_SWITCH_OFF, player);
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
    }

    private static void vanishNotify(NotifyMessage message, Player targetPlayer) {
        MessageChannel.permission(notifyPermission).send(Text.builder().append(Text.of(String.join(message.getMessage(), targetPlayer.getName()))).build());
    }
}