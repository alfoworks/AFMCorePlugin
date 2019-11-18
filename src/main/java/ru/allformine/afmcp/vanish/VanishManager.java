package ru.allformine.afmcp.vanish;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class VanishManager {
    public static final String vanishPermission = "afmcp.vanish.staff";

    public static List<String> playersToRemove = new ArrayList<>();
    public static List<Player> vanishedPlayers = new ArrayList<>();
    private static List<Player> couldInteract = new ArrayList<>();

    public static VanishTabList tabList = new VanishTabList();

    public static boolean isVanished(Player player) {
        return vanishedPlayers.contains(player);
    }

    public static boolean canInteract(Player player) {
        return couldInteract.contains(player);
    }

    @SuppressWarnings("WeakerAccess")
    public static void makeCanInteract(Player player){
        couldInteract.add(player);
    }

    @SuppressWarnings("WeakerAccess")
    public static void makeCantInteract(Player player){
        couldInteract.remove(player);
    }

    public static boolean switchCanInteract(Player player){
        if(canInteract(player)){
            makeCantInteract(player);
            return false;
        }
        makeCanInteract(player);
        return true;
    }

    public static void vanishPlayer(Player player, boolean onJoin) {
        setVanish(player, true, onJoin);

        tabList.removeTabListPlayer(player.getName());

        vanishedPlayers.add(player);

        vanishNotify(String.format(onJoin ? "%s скрытно вошёл в игру" : "%s вошел в ваниш", player.getName()));
    }

    public static void unvanishPlayer(Player player, boolean onLeave) {
        setVanish(player, false, onLeave);

        vanishedPlayers.remove(player);

        vanishNotify(String.format(onLeave ? "%s вышел из игры (персонал)" : "%s вышел из ваниша", player.getName()));

        if (!onLeave) {
            tabList.addTabListPlayer(player.getName());
        }
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
    }

    private static void vanishNotify(String message) {
        MessageChannel.permission(vanishPermission).send(Text.builder().append(Text.of(message)).color(TextColors.DARK_AQUA).build());
    }
}