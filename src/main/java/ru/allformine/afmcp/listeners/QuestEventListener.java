package ru.allformine.afmcp.listeners;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.eaglefactions.common.listeners.PlayerJoinListener;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.EventContextKeys;
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
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.quests.PlayerContribution;
import ru.allformine.afmcp.quests.Quest;
import ru.allformine.afmcp.quests.QuestTarget;

import java.net.ConnectException;
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
                logger.error("No quest data");
                return;
            }

            Player playerR = (Player) player;
            PlayerContribution contribution = AFMCorePlugin.questDataManager.getContribution(playerR.getUniqueId());

            // Quest data
            List<Text> lore = questData.get(Keys.ITEM_LORE).orElse(null);
            int questId = -1;
            int questLevel = contribution.getCompletedQuests().length / 25 + 1;

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
                        logger.debug("Apply final");
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
                    quest.getTarget().setProgress(quest.getTarget().getCount());
                    contribution.completeQuest(quest);
                    contribution.resetCompletedQuests(temp);
                    AFMCorePlugin.questDataManager.updateContribution(contribution, "u");

                    AFMCorePlugin.questDataManager.closeGUI(playerR, event);
                    Text message = Text.of(TextColors.RED, "QUEST HAS BEEN ABORTED");
                    playerR.sendMessage(message);
                    logger.debug("Abort final");
                }
            }
        }
    }

    // Quest Tracker

    // Load Quest Tracker
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        UUID uuid = player.getUniqueId();
        PlayerContribution contribution = AFMCorePlugin.questDataManager.getContribution(uuid);
        if (!questTracker.containsKey(uuid)) {
            questTracker.put(uuid, contribution.getActiveQuests());
            logger.debug(String.format("Loaded %s quest data",
                    player.getName()));

            // Useless data here. Can be removed
            int size = 0;
            for (Quest q: contribution.getActiveQuests())
                if (q != null)
                    size++;

            logger.debug(String.format("Have %s active quests", size));
        } else {
            throw new AssertionError(String.format("Player %s haven't unloaded questTracker", player.getName()));
        }
    }

    // Unload Quest Tracker
    @Listener
    public void unloadQuestTracker(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        UUID uuid = player.getUniqueId();
        if (questTracker.containsKey(uuid)) {
            questTracker.remove(uuid);
            logger.debug(String.format("Unloaded %s quest data",
                    player.getName()));
        } else {
            throw new AssertionError(String.format("Player %s hadn't created his questTracker", player.getName()));
        }
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
                            if (quest.getType().equals("entity")) {
                                QuestTarget questTarget = quest.getTarget();
                                if (questTarget.getProgress() < questTarget.getCount()) {
                                    questTarget.appendProgress(1);
                                    quest.setRawTarget(questTarget);

                                    PlayerContribution contribution =
                                            AFMCorePlugin.questDataManager.getContribution(player.getUniqueId());
                                    contribution.updateQuest(quest);

                                    AFMCorePlugin.questDataManager.updateContribution(contribution, "u");

                                    if (questTarget.finished())
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

    //// TODO: Item Gather quest
}
