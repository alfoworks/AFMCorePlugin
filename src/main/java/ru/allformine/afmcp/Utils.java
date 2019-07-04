package ru.allformine.afmcp;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

public class Utils {
    public static boolean isSneaking(Player player) {
        return player.get(Keys.IS_SNEAKING).orElse(false);
    }
}
