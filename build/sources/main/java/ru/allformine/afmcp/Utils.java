package ru.allformine.afmcp;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

public class Utils {
    public static boolean isSneaking(Player player) {
        return player.get(Keys.IS_SNEAKING).orElse(false);
    }

    public static void sendNotifyWithSoundToAll(String title, String subtitle) {
        Title titleToSend = Title.builder()
                .title(Text.of(title))
                .subtitle(Text.of(subtitle))
                .build();

        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.getPosition(), 1, 0.5);
            player.sendTitle(titleToSend);
        }
    }

    public static void afmRestart() {
        AFMCorePlugin.serverRestart = true;
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            player.kick(Text.builder("Сервер ушёл на рестарт! Увидимся через минуту <3").color(TextColors.LIGHT_PURPLE).build());
        }

        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "stop");
    }
}
