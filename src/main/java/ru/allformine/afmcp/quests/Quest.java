package ru.allformine.afmcp.quests;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

// This is dataclass which represents quest
public class Quest {
    private final String name;
    private final String type;
    private final String target;
    private final String startMessage;
    private final String finalMessage;
    private final String lore;
    private final Date questEnd;
    private final int count;
    private UUID parent;
    private int progress;


    // These setters are for GSON parser to work
    public Quest(String name,
                 String type,
                 String target,
                 String startMessage,
                 String finalMessage,
                 String lore,
                 Date questEnd,
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
        this.questEnd = questEnd;
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

    public String getName() {
        return name;
    }

    public String getTarget() {
        return target;
    }

    public boolean finished() {
        return progress >= count;
    }

    public String getStartMessage() {
        return startMessage;
    }

    public String getFinalMessage() {
        return finalMessage;
    }

    public String getLore() {
        return lore;
    }

    public Date getQuestEnd() {
        return questEnd;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID uuid) {
        this.parent = uuid;
    }
}