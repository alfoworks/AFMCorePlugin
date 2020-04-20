package ru.allformine.afmcp.quests;

import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;

// Dataclass which represents target of a quest
// Entity / Item
public class QuestTarget {
    private final Object target;
    private final int count;
    private final int priority;
    private int progress;

    public QuestTarget(Object target, int count, int priority) {
        this.target = target;
        this.count = count;
        this.priority = priority;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getPriority() {
        return this.priority;
    }

    public int getCount() {
        return this.count;
    }

    // Be aware of NullPointerException!
    public Object getTarget() {
        return this.target;
    }
}
