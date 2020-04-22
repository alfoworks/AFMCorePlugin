package ru.allformine.afmcp.quests;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import ru.allformine.afmcp.AFMCorePlugin;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class QuestGUI {
    private Inventory bakeGui(PlayerContribution data, int id) {
        Inventory inventory = Inventory.builder().build(Objects.requireNonNull(Sponge.getPluginManager().getPlugin("afmcp").orElse(null)));

        ItemStack LVL;
        ItemStack[] QeS;
        ItemStack LOR;
        ItemStack YES;
        ItemStack NOO;

        ItemType questAllow = ItemTypes.BOOK;
        ItemType questActive = ItemTypes.WRITABLE_BOOK;
        ItemType questComplete = ItemTypes.ENCHANTED_BOOK;
        ItemType applyButton = ItemTypes.EMERALD_BLOCK;
        ItemType denyButton = ItemTypes.REDSTONE_BLOCK;
        ItemType loreData = ItemTypes.MAP;

        /* Quest List
        LVL QeS QeS QeS QeS QeS QeS QeS QeS
        QeS QeS QeS QeS QeS QeS QeS QeS QeS
        QeS QeS QeS QeS QeS QeS QeS QeS LVL
         */

        /* Begin quest %questName%?
        LVL Air Air Air QeS Air Air Air LVL
        Air Air YES Air LOR Air NOO Air Air
        LVL Air Air Air Air Air Air Air LVL
         */

        // Determine lvl by switch case
        // Get quest lvl from / 25 of completed quests
        int questMax = 25;
        boolean ignore = false;
        boolean ignore1 = false;

        int questLvl;
        try {
            questLvl = data.getCompletedQuests().length / questMax + 1;
        } catch (NullPointerException e) {
            questLvl = 1;
            ignore = true;
        }

        try {
            data.getActiveQuests();
        } catch (NullPointerException e) {
            ignore1 = true;
        }

        switch (questLvl) {
            case 1:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.GRASS)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "Level 1"));
                break;

            case 2:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.STONE)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, TextStyles.BOLD, "Level 2"));
                break;

            case 3:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.COAL)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_GRAY, TextStyles.BOLD, "Level 3"));
                break;

            case 4:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.IRON_INGOT)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, TextStyles.BOLD, "Level 4"));
                break;

            case 5:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.GOLD_INGOT)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Level 5"));
                break;

            case 6:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.DIAMOND)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, TextStyles.BOLD, "Level 6"));
                break;

            case 7:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.TNT)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, TextStyles.BOLD, "Level 7"));
                break;

            case 8:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.OBSIDIAN)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_BLUE, TextStyles.BOLD, "Level 8"));
                break;

            case 9:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.BLAZE_POWDER)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, TextStyles.BOLD, "Level 9"));
                break;

            case 10:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.ENDER_EYE)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.LIGHT_PURPLE, TextStyles.BOLD, "Level 10"));
                break;

            case 11:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.NETHER_STAR)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_RED, TextStyles.BOLD, "Level 11"));
                break;

            case 12:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.BEDROCK)
                        .build();
                LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.BLACK, TextStyles.BOLD, "Level 12"));
                break;

            default:
                LVL = ItemStack.builder()
                        .itemType(ItemTypes.BARRIER)
                        .build();
        }

        QeS = new ItemStack[questMax];
        for (int x = 0; x < questMax-1; x++) {
            System.err.println(x);
            Quest quest = AFMCorePlugin.questDataManager.getQuest(questLvl, x);
            QuestTarget target = quest.getTarget();
            String questName = quest.getName();

            // Progress format
            Text progressText = Text.of(TextColors.DARK_GREEN, "Begin Quest");
            ItemType status = questAllow;

            int progress = target.getProgress();
            int count = target.getCount();

            if (!ignore) {
                for (Quest q: data.getCompletedQuests()) {
                    if (quest.getName().equals(q.getName())) {
                        progressText = Text.of(TextColors.GREEN, TextStyles.ITALIC, "Quest Complete");
                        status = questComplete;
                    }
                }
            }

            if (!ignore1) {
                for (Quest q: data.getActiveQuests()) {
                    if (quest.getName().equals(q.getName())) {
                        String typeMessage;
                        if (q.getType().equals("entity"))
                            typeMessage = "killed";
                        else
                            typeMessage = "gathered";

                        progressText = Text.of(TextColors.GREEN, String.format("%s/%s %s", count, progress, typeMessage));
                        status = questActive;
                    }
                }
            }

            ItemStack result = ItemStack.builder()
                    .itemType(status)
                    .build();
            result.offer(Keys.DISPLAY_NAME, progressText);
            QeS[x] = result;
        }

        Iterator<Inventory> iterator = inventory.slots().iterator();
        int slotN = 0;

        while (iterator.hasNext()) {
            if (id == -1)
                if (slotN == 0 || slotN == 9*3-1)
                    inventory.set(LVL);
                else
                    inventory.set(QeS[slotN-1]);

            slotN++;
            iterator.next();
        }
        return inventory;
    }

    public void showToPlayer(PlayerContribution playerContribution, Player player, int id) {
        player.openInventory(bakeGui(playerContribution, id));
    }
}
