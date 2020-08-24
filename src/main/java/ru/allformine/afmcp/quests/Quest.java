package ru.allformine.afmcp.quests;

import org.spongepowered.api.text.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

// This is dataclass which represents quest
public class Quest {
    private final Text name;
    private final String type;
    private final String target;
    private final Text startMessage;
    private final Text finalMessage;
    private final Text lore;
    private final int count;
    private UUID parent;
    private int progress;


    // These setters are for GSON parser to work
    public Quest(Text name,
                 String type,
                 String target,
                 Text startMessage,
                 Text finalMessage,
                 Text lore,
                 int count,
                 UUID parent) {
        this.name = name;
        this.type = type;
        this.target = target;
        this.startMessage = startMessage;
        this.finalMessage = finalMessage;
        this.lore = lore;
        this.count = count;
        this.parent = parent;
        this.progress = 0;
    }

    public int getCount() {
        return count;
    }

    public void appendProgress(int appendix) {
        progress += appendix;
    }

    public void setProgress(int x) {
        progress = x;
    }

    public int getProgress() {
        return progress;
    }

    public String getType() {
        return type;
    }

    public Text getName() {
        return name;
    }

    public String getTarget() {
        return target;
    }

    public boolean finished() {
        return progress >= count;
    }

    public Text getStartMessage() {
        return startMessage;
    }

    public Text getFinalMessage() {
        return finalMessage;
    }

    public Text getLore() {
        return lore;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID uuid) {
        this.parent = uuid;
    }
}