package ru.allformine.afmcp.quests;

import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

// This is dataclass which represents quest
public class Quest {
    private String type;
    private String target;
    private int count;
    private int priority;
    private QuestTarget qTarget;

    private void setQTarget() {
        // Getting actual target type from Strings
        try
        {
            // Raises RuntimeException if creation has been failed
            EntityType entity = (getType().equals("entity"))
                    ? DummyObjectProvider.createFor(EntityType.class, target) : null;
            ItemType item = (getType().equals("item"))
                    ? DummyObjectProvider.createFor(ItemType.class, target) : null;


            if (entity != null) {
                this.qTarget = new QuestTarget(entity, count, priority);
            } else {
                this.qTarget = new QuestTarget(item, count, priority);
            }
        }
        catch (RuntimeException e)
        {
            throw new AssertionError("Quests JSON is corrupted. Contact plugin developer to fix");
        }
    }

    // These setters are for GSON parser to work
    public void setCount(int count) {
        this.count = count;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public QuestTarget getTarget() {
        if (this.qTarget == null) {
            setQTarget();
        }

        return this.qTarget;
    }
}