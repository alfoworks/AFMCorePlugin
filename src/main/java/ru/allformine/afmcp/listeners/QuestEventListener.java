package ru.allformine.afmcp.listeners;

import net.minecraft.util.datafix.walkers.ItemStackData;
import org.slf4j.Logger;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.quests.PlayerContribution;
import ru.allformine.afmcp.quests.Quest;

import java.util.*;

public class QuestEventListener {
    private final Logger logger = AFMCorePlugin.logger;
    private Map<UUID, Quest[]> questTracker = new HashMap<>();

    @Listener
    public void ClickInventoryEvent(ClickInventoryEvent event) {
        Text originTitle = event.getTargetInventory().getInventoryProperty(InventoryTitle.class)
                .orElse(InventoryTitle.of(Text.of(""))).getValue();
        if (Objects.equals(originTitle, Text.of(TextColors.YELLOW, "Quest Menu"))) {
            event.setCancelled(true);

            Object player = event.getSource();
            assert player instanceof Player;

            Slot slot = event.getTransactions().get(0).getSlot();
            SlotIndex slotIndex = slot.getInventoryProperty(SlotIndex.class).orElse(null);
            int questId = slotIndex.getValue();

            if (!(questId == 0 || questId == 26)) {
                AFMCorePlugin.questDataManager.openGUI((Player) player, questId, event);
            }
        } else if (Objects.equals(originTitle, Text.of(TextColors.DARK_GREEN, "Begin Quest?"))) {
            event.setCancelled(true);

            Object player = event.getSource();
            assert player instanceof Player;

            Text slotName = null;

            for (SlotTransaction t: event.getTransactions()) {
                ItemStack s = t.getOriginal().createStack();

                Optional<Text> text = s.get(Keys.DISPLAY_NAME);
                if (text.isPresent()) {
                    slotName = text.get();
                } else {
                    logger.error("No text data");
                }
            }

            ItemStack questData = null;

            for (Inventory i: event.getTargetInventory().slots()) {
                Optional<SlotIndex> slot;
                slot = i.getInventoryProperty(SlotIndex.class);
                if (slot.isPresent()) {
                    int slotIndex = slot.get().getValue();
                    Optional<ItemStack> item = i.peek();
                    if (!item.isPresent())
                        continue;

                    if (slotIndex == 4)
                        questData = item.get();
                } else {
                    logger.error("No slot data");
                }
            }

            if (questData == null) {
                event.setCancelled(true);
                return;
            }

            Player playerR = (Player) player;
            PlayerContribution contribution = AFMCorePlugin.questDataManager.getContribution(playerR.getUniqueId());

            // Quest data
            List<Text> lore = questData.get(Keys.ITEM_LORE).orElse(null);
            int questId = -1;
            int questLevel = 1;
            if (contribution.getCompletedQuests() != null) {
                questLevel = contribution.getCompletedQuests().length / 25 + 1;;
            }

            if (lore != null) {
                questId = Integer.parseInt(lore.get(lore.size()-1).toPlainSingle());
            }
            if (slotName != null) {
                if (slotName.equals(Text.of(TextColors.GREEN, "Apply"))) { // Apply click
                    if (contribution.assignQuest(AFMCorePlugin.questDataManager.getQuest(questLevel, questId))) {
                        AFMCorePlugin.questDataManager.updateContribution(contribution, "u");
                        AFMCorePlugin.questDataManager.closeGUI(playerR, event);
                        Text message = Text.of(TextColors.YELLOW, "WORK IN PROGRESS");
                        playerR.sendMessage(message);

                        updateQuestTracker(contribution);
                        logger.debug("Apply final");
                    } else {
                        playerR.kick(Text.of(TextColors.RED, "ТЫ ЕБЛАН Я ТВОЮ МАТЬ ЕБАЛ"));
                    }
                } else if (slotName.equals(Text.of(TextColors.RED, "Deny"))) { // Deny click
                    AFMCorePlugin.questDataManager.openGUI(playerR, -1, event);
                    logger.debug("Deny final");
                } else if (slotName.equals(Text.of(TextColors.GREEN, "Continue"))) {
                    AFMCorePlugin.questDataManager.openGUI(playerR, -1, event);
                    logger.debug("Continue final");
                } else if (slotName.equals(Text.of(TextColors.RED, "Abort"))) {
                    Quest[] temp = contribution.getCompletedQuests();
                    String name = AFMCorePlugin.questDataManager.getQuest(questLevel, questId).getName();

                    Quest quest = contribution.getQuest(name);
                    quest.setProgress(quest.getCount());
                    contribution.completeQuest(quest);
                    contribution.resetCompletedQuests(temp);
                    AFMCorePlugin.questDataManager.updateContribution(contribution, "u");

                    AFMCorePlugin.questDataManager.closeGUI(playerR, event);
                    Text message = Text.of(TextColors.RED, "QUEST HAS BEEN ABORTED");
                    playerR.sendMessage(message);

                    updateQuestTracker(contribution);
                    logger.debug("Abort final");
                }
            }
        }
    }

    // Quest Tracking system

    private void updateQuestTracker(PlayerContribution contribution) {
        if (!questTracker.containsKey(contribution.getPlayer())) {
            loadQuestTracker(contribution);
        }
        questTracker.replace(contribution.getPlayer(), contribution.getActiveQuests());
        logger.debug(String.format("Updated %s quest data",
                contribution.getPlayer()));
    }

    private void loadQuestTracker(PlayerContribution contribution) {
        if (!questTracker.containsKey(contribution.getPlayer())) {
            questTracker.put(contribution.getPlayer(), contribution.getActiveQuests());
            logger.debug(String.format("Loaded %s quest data",
                    contribution.getPlayer()));
        } else {
            throw new AssertionError(String.format("Player %s hadn't unloaded questTracker", contribution.getPlayer()));
        }
    }

    private void unloadQuestTracker(Player player) {
        if (questTracker.containsKey(player.getUniqueId())) {
            questTracker.remove(player.getUniqueId());
            logger.debug(String.format("Unloaded %s quest data",
                    player.getName()));
        } else {
            throw new AssertionError(String.format("Player %s hadn't created his questTracker", player.getName()));
        }
    }

    // Player join/leave updater
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        UUID uuid = player.getUniqueId();
        loadQuestTracker(AFMCorePlugin.questDataManager.getContribution(uuid));
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        unloadQuestTracker(player);
    }

    // Entity Kill quest
    @Listener
    public void DamageEntityEvent(DamageEntityEvent event) {
        if (event.willCauseDeath()) {
            if (event.getCause().first(Player.class).isPresent()) {
                Player player = event.getCause().first(Player.class).get();
                UUID uuid = player.getUniqueId();
                if (questTracker.containsKey(uuid)) {
                    Quest[] quests = questTracker.get(uuid);
                    for (Quest quest : quests) {
                        if (quest != null) {
                            if (quest.getTarget().equals(event.getTargetEntity().getType().getId())) {
                                if (quest.getProgress() < quest.getCount()) {
                                    quest.appendProgress(1);

                                    PlayerContribution contribution =
                                            AFMCorePlugin.questDataManager.getContribution(player.getUniqueId());
                                    contribution.updateQuest(quest);

                                    AFMCorePlugin.questDataManager.updateContribution(contribution, "u");

                                    if (quest.finished())
                                        //// TODO: Quest Complete Event
                                        player.getMessageChannel().send(Text.of(TextColors.GREEN, "НИХУЯ ТЫ КВЕСТ ПРОШЕЛ"));
                                } /*else {
                                    // not possible
                                }*/
                            }
                        }
                    }

                    questTracker.replace(uuid, quests);
                }
            }
        }
    }

    // Item Gather quest
    @Listener
    public void ItemPickupEvent(ChangeInventoryEvent.Pickup.Pre event) {
        if (event.getTargetEntity().isOnGround()) {
            if (event.getCause().first(Player.class).isPresent()) {
                Player player = event.getCause().first(Player.class).get();
                if (questTracker.containsKey(player.getUniqueId())) {
                    Quest[] quests = questTracker.get(player.getUniqueId());
                    for (Quest quest: quests) {
                        if (quest != null) {
                            if (quest.getTarget().equals(event.getOriginalStack().getType().getName())) {
                                ItemStackSnapshot item = event.getOriginalStack();
                                int count = item.getQuantity();
                                List<ItemStackSnapshot> custom = new ArrayList<>();
                                custom.add(
                                        ItemStack.builder()
                                                .itemType(ItemTypes.PAPER)
                                                .add(Keys.DISPLAY_NAME,
                                                        Text.of(TextColors.GRAY, "Предмет ушел в уплату квеста"))
                                                .quantity(count)
                                                .build()
                                                .createSnapshot()
                                );
                                event.setCustom(custom);
                                if (quest.getProgress() < quest.getCount()) {
                                    quest.appendProgress(count);

                                    PlayerContribution contribution =
                                            AFMCorePlugin.questDataManager.getContribution(player.getUniqueId());
                                    contribution.updateQuest(quest);

                                    AFMCorePlugin.questDataManager.updateContribution(contribution, "u");

                                    if (quest.finished())
                                        //// TODO: Quest Complete Event
                                        player.getMessageChannel().send(Text.of(TextColors.GREEN, "НИХУЯ ТЫ КВЕСТ ПРОШЕЛ"));

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
