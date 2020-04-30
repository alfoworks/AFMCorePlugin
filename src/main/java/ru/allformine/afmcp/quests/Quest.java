package ru.allformine.afmcp.quests;

import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

// This is dataclass which represents quest
public class Quest {
    private String name;
    private String type;
    private String target;
    private int count;
    private int priority;
    private QuestTarget qTarget;

    private void setQTarget() {
        // Getting actual target type from Strings
        try {
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
        } catch (RuntimeException e) {
            throw new AssertionError("Quests JSON is corrupted. Contact plugin developer to fix");
        }
    }

    // These setters are for GSON parser to work
    public Quest(String name, String type, String target, int count, int priority) {
        this.name = name;
        this.type = type;
        this.target = target;
        this.count = count;
        this.priority = priority;
    }

    public String toString() {
        return type += getTarget() + name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setRawTarget(QuestTarget questTarget) {
        this.qTarget = questTarget;
    }

    public QuestTarget getTarget() {
        if (this.qTarget == null) {
            setQTarget();
        }

        return this.qTarget;
    }
}