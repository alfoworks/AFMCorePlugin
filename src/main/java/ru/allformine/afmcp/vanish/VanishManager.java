package ru.allformine.afmcp.vanish;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class VanishManager {
    public static final String vanishPermission = "afmcp.vanish.staff";

    public static List<String> playersToRemove = new ArrayList<>();
    public static List<Player> vanishedPlayers = new ArrayList<>();
    private static Set<Player> playerInteractAbilities = new HashSet<>(); //TODO RENAME

    public static boolean isVanished(Player player) {
        return vanishedPlayers.contains(player);
    }


    // TODO RENAME
    public static boolean canInteract(Player player) {
        return playerInteractAbilities.contains(player);
    }

    // TODO RENAME
    public static void makeCanInteract(Player player){
        playerInteractAbilities.add(player);
    }

    // TODO RENAME
    public static void makeCantInteract(Player player){
        playerInteractAbilities.remove(player);
    }

    public static boolean switchCanInteract(Player player){
        if(canInteract(player)){
            makeCantInteract(player);
            return false;
        }else{
            makeCanInteract(player);
            return true;
        }
    }

    public static void vanishPlayer(Player player, boolean onJoin) {
        setVanish(player, true, onJoin);

        vanishedPlayers.add(player);

        vanishNotify(String.format(onJoin ? "%s скрытно вошёл в игру" : "%s вошел в ваниш", player.getName()));
        Optional<List<PotionEffect>> effects = player.get(Keys.POTION_EFFECTS);//.get();
        if(effects.isPresent()) {
            effects.get().clear();
            player.offer(Keys.POTION_EFFECTS, effects.get());
        }

        updateTabLists();
    }

    public static void unvanishPlayer(Player player, boolean onLeave) {
        setVanish(player, false, onLeave);

        vanishedPlayers.remove(player);
        playerInteractAbilities.remove(player);
        vanishNotify(String.format(onLeave ? "%s вышел из игры (персонал)" : "%s вышел из ваниша", player.getName()));

        updateTabLists();
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

    @SuppressWarnings("WeakerAccess")
    public static void updateTabLists() { //TODO рефактор. Я очень жидко здесь насрал
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            TabList tabList = player.getTabList();

            for (TabListEntry entry : tabList.getEntries()) {
                tabList.removeEntry(entry.getProfile().getUniqueId());
            }

            for (Player p : Sponge.getServer().getOnlinePlayers()) {
                if (isVanished(p)) continue;

                tabList.addEntry(getTabListEntryForPlayer(p, tabList));
            }
        }
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

    private static TabListEntry getTabListEntryForPlayer(Player player, TabList list) {
        return TabListEntry.builder()
                .list(list)
                .gameMode(player.gameMode().get())
                .profile(player.getProfile())
                .latency(player.getConnection().getLatency())
                .displayName(Text.of(player.getName()))
                .build();
    }
}