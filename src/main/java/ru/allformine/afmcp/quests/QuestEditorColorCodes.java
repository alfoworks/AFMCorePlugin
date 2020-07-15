package ru.allformine.afmcp.quests;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class QuestEditorColorCodes {

    public static final Map<String, TextColor> values;
    static {
        Map<String, TextColor> map = new HashMap<>();
        map.put("AQUA", TextColors.AQUA);
        map.put("BLACK", TextColors.BLACK);
        map.put("BLUE", TextColors.BLUE);
        map.put("DARK_AQUA", TextColors.DARK_AQUA);
        map.put("DARK_BLUE", TextColors.DARK_BLUE);
        map.put("DARK_GRAY", TextColors.DARK_GRAY);
        map.put("DARK_GREEN", TextColors.DARK_GREEN);
        map.put("DARK_PURPLE", TextColors.DARK_PURPLE);
        map.put("DARK_RED", TextColors.DARK_RED);
        map.put("GOLD", TextColors.GOLD);
        map.put("GRAY", TextColors.GRAY);
        map.put("YELLOW", TextColors.YELLOW);
        map.put("GREEN", TextColors.GREEN);
        map.put("LIGHT_PURPLE", TextColors.LIGHT_PURPLE);
        map.put("RED", TextColors.RED);
        values = Collections.unmodifiableMap(map);
    }

}
