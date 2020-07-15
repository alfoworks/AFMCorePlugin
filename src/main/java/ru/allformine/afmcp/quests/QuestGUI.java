package ru.allformine.afmcp.quests;

import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import ru.allformine.afmcp.AFMCorePlugin;

import java.lang.reflect.Array;
import java.util.*;

public class QuestGUI {

    private Inventory bakeGui(PlayerContribution data, int id) {
        Inventory inventory = Inventory.builder()
                .property("inventorytitle", new InventoryTitle(
                        (id == -1) ?
                                Text.of(TextColors.YELLOW, "Quest Menu") :
                                Text.of(TextColors.DARK_GREEN, "Quest Panel")))
                .build(Objects.requireNonNull(Sponge.getPluginManager().getPlugin("afmcp").orElse(null)));

        ItemStack LVL;
        ItemStack[] QeS;
        ItemStack LOR;
        ItemStack YES;
        ItemStack NOO;
        ItemStack INP;

        ItemType questAllow = ItemTypes.BOOK;
        ItemType questActive = ItemTypes.WRITABLE_BOOK;
        ItemType questComplete = ItemTypes.ENCHANTED_BOOK;
        ItemType applyButton = ItemTypes.EMERALD_BLOCK;
        ItemType denyButton = ItemTypes.REDSTONE_BLOCK;
        ItemType nothingButton = ItemTypes.COAL_BLOCK;
        ItemType loreData = ItemTypes.MAP;
        ItemType inputField = ItemTypes.ENDER_CHEST;

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

        QuestLevel questLvl = data.getLevel();

        try {
            data.getActiveQuests()[0].getName();
        } catch (NullPointerException e) {
            ignore1 = true;
        }

        Optional<ItemType> lvlType = Sponge.getGame().getRegistry().getType(CatalogTypes.ITEM_TYPE, questLvl.getItemTypeId());

        LVL = ItemStack.builder()
                .itemType(lvlType.orElse(ItemTypes.BARRIER))
                .build();
        LVL.offer(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_RED, TextStyles.BOLD, questLvl.getLevelId()));

        QeS = new ItemStack[questMax];
        for (int x = 0; x < questMax; x++) {
            Quest quest = questLvl.getQuest(x);
            String questName = quest.getName().toPlain();

            // Progress format
            Text progressText = Text.of(TextColors.DARK_GREEN, "Begin Quest");
            ItemType status = questAllow;

            // If completed quests are present
            if (!ignore) {
                for (Quest q: data.getCompletedQuests()) {
                    if (quest.getName().equals(q.getName())) {
                        progressText = Text.of(TextColors.GREEN, TextStyles.ITALIC, "Quest Complete");
                        status = questComplete;
                    }
                }
            }

            // If active quests are present
            if (!ignore1) {
                for (Quest q: data.getActiveQuests()) {
                    if (q == null) continue;
                    if (quest.getName().equals(q.getName())) {
                        int progress = q.getProgress();
                        int count = q.getCount();

                        String typeMessage = q.getTarget().split(":")[1];;
                        typeMessage = typeMessage.substring(0, 1).toUpperCase() + typeMessage.substring(1); // Capitalizing

                        if (q.getType().equals("entity")) {
                            typeMessage += " killed";
                        }
                        else {
                            typeMessage = q.getTarget();
                            typeMessage += " gathered";
                        }

                        progressText = Text.of(TextColors.GREEN, String.format("%s/%s %s", progress, count, typeMessage));
                        status = questActive;
                    }
                }
            }

            List<Text> lore = new ArrayList<>();
            lore.add(progressText);
            lore.add(Text.of(TextColors.BLACK, x));

            ItemStack result = ItemStack.builder()
                    .itemType(status)
                    .build();
            result.offer(Keys.DISPLAY_NAME, Text.of(questName));
            result.offer(Keys.ITEM_LORE, lore);
            QeS[x] = result;
        }

        Iterable<Inventory> slots = inventory.slots();
        int slotN = 0;

        YES = ItemStack.builder()
                .itemType(applyButton)
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Apply"))
                .build();


        NOO = ItemStack.builder()
                .itemType(denyButton)
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Deny"))
                .build();

        INP = ItemStack.builder()
                .itemType(inputField)
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, "Insert quest items here"))
                .build();

        if (id != -1) {
            List<Text> lore = new ArrayList<>();
            lore.add(Text.of(questLvl.getQuest(id).getLore()));

            LOR = ItemStack.builder()
                    .itemType(loreData)
                    .add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "Description"))
                    .add(Keys.ITEM_LORE, lore)
                    .build();
        } else {
            LOR = null;
        }

        // Check if quest has begun
        // If true - show player quest data, abort and continue options
        boolean currentActive = false;
        boolean currentComplete = false;
        boolean item = false;
        if (id != -1 ) {
            Quest current = questLvl.getQuest(id-1);
            item = current.getType().equals("item");
            currentActive = Arrays.stream(data.getActiveQuests()).anyMatch(q ->
            { if (q != null)
                return q.getName().equals(current.getName());
                return false;
            });
            if (data.getCompletedQuests() != null) {
                currentComplete = Arrays.stream(data.getCompletedQuests()).anyMatch(q ->
                { if (q != null)
                    return q.getName().equals(current.getName());
                    return false;
                });
            }
        }

        AFMCorePlugin.logger.debug("----QUEST BUILD----");
        for (Inventory slot: slots) {
            if (id == -1) {
                if (slotN == 0 || slotN == 9 * 3 - 1) {
                    slot.set(LVL);
                    AFMCorePlugin.logger.debug("Adding LVL");
                } else {
                    slot.set(QeS[slotN - 1]);
                    AFMCorePlugin.logger.debug("Adding QeS");
                }
            } else if (currentComplete) {
                if (slotN == 8 || slotN == 0 || slotN == 18 || slotN == 26) {
                    slot.set(LVL);
                    AFMCorePlugin.logger.debug("Adding LVL");
                } else if (slotN == 4) {
                    slot.set(QeS[id-1]);
                    AFMCorePlugin.logger.debug("Adding QeS");
                } else if (slotN == 11) {
                    YES.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Continue"));
                    slot.set(YES);
                    AFMCorePlugin.logger.debug("Adding CON");
                } else if (slotN == 13) {
                    slot.set(LOR);
                    AFMCorePlugin.logger.debug("Adding LOR");
                } else if (slotN == 15) {
                    NOO = ItemStack.builder()
                            .itemType(nothingButton)
                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_GRAY, "Abort"))
                            .build();

                    slot.set(NOO);
                    AFMCorePlugin.logger.debug("Adding NOT");
                }
            } else if (!currentActive) {
                if (slotN == 8 || slotN == 0 || slotN == 18 || slotN == 26) {
                    slot.set(LVL);
                    AFMCorePlugin.logger.debug("Adding LVL");
                } else if (slotN == 4) {
                    slot.set(QeS[id - 1]);
                    AFMCorePlugin.logger.debug("Adding QeS");
                } else if (slotN == 11) {
                    slot.set(YES);
                    AFMCorePlugin.logger.debug("Adding YES");
                } else if (slotN == 13) {
                    slot.set(LOR);
                    AFMCorePlugin.logger.debug("Adding LOR");
                } else if (slotN == 15) {
                    slot.set(NOO);
                    AFMCorePlugin.logger.debug("Adding NOO");
                }
            } else {
                if (slotN == 8 || slotN == 0 || slotN == 18 || slotN == 26) {
                    slot.set(LVL);
                    AFMCorePlugin.logger.debug("Adding LVL");
                } else if (slotN == 4) {
                    slot.set(QeS[id-1]);
                    AFMCorePlugin.logger.debug("Adding QeS");
                } else if (slotN == 11) {
                    YES.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Continue"));
                    slot.set(YES);
                    AFMCorePlugin.logger.debug("Adding CON");
                } else if (slotN == 13 && !item) {
                    slot.set(LOR);
                    AFMCorePlugin.logger.debug("Adding LOR");
                } else if (slotN == 13) {
                    slot.set(INP);
                    AFMCorePlugin.logger.debug("Adding INP");
                } else if (slotN == 15) {
                    NOO.offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Abort"));
                    slot.set(NOO);
                    AFMCorePlugin.logger.debug("Adding ABO");
                } else if (slotN == 22 && item) {
                    slot.set(LOR);
                    AFMCorePlugin.logger.debug("Adding LOR");
                }
            }
            slotN++;
        }
        AFMCorePlugin.logger.debug("----QUEST FINAL----");

        return inventory;
    }

    public void showToPlayer(PlayerContribution playerContribution, Player player, int id) {
        player.openInventory(bakeGui(playerContribution, id));
    }

    /**
     * Should be called after inventory interaction.
     *
     * @param playerContribution data to be used in baking gui
     * @param player to show to this player
     * @param id quest id, usually it's clicked slot ID
     * @param event using it we can determine should we cancel event or not
     */
    public void showToPlayer(PlayerContribution playerContribution, Player player, int id, ClickInventoryEvent event) {
        if (id < 26) {
            Task.builder()
                    .execute(() -> {
                        player.closeInventory();
                        player.openInventory(bakeGui(playerContribution, id));
                    })
                    .submit(Sponge.getPluginManager().getPlugin("afmcp").get().getInstance().get());
        } else {
            event.setCancelled(false);
        }
    }

    public void closeGUI(Player player, ClickInventoryEvent event) {
        Task.builder()
                .execute(player::closeInventory)
                .submit(Sponge.getPluginManager().getPlugin("afmcp").get().getInstance().get());
    }
}
