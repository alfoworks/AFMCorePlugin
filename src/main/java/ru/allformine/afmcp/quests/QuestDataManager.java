package ru.allformine.afmcp.quests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.command.CommandException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import ru.allformine.afmcp.AFMCorePlugin;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class QuestDataManager {
    private Logger logger = AFMCorePlugin.logger;

    private QuestLevel[] questDifficulties;
    private Object factionContributions;
    private final Path factionPath;
    private final QuestGUI gui;

    // Constructs both data files
    public QuestDataManager(Path questsPath, Path factionsPath) {
        this.factionPath = factionsPath;
        this.gui = new QuestGUI();
        this.questDifficulties = getQuests(questsPath);;
    }

    public QuestFactionContainer getQuestFactions() {
        String jsonData = null;
        try {
            jsonData = new String(Files.readAllBytes(factionPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert jsonData != null;
        if (jsonData.equals(""))
            return new QuestFactionContainer();


        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(QuestFactionContainer.class, new QuestFactionContainerDeserializer())
                .registerTypeAdapter(QuestFaction.class, new QuestFactionDeserializer())
                .registerTypeAdapter(PlayerContribution.class, new PlayerContributionDeserializer())
                .registerTypeAdapter(Quest.class, new QuestDeserializer())
                .create();

        return gson.fromJson(jsonData, QuestFactionContainer.class);
    }

    private QuestLevel[] getQuests(Path path) {
        String jsonData = null;
        try {
            jsonData = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(QuestLevel[].class, new QuestLevelDeserializer())
                .registerTypeAdapter(Quest.class, new QuestDeserializer())
                .create();

        return gson.fromJson(jsonData, QuestLevel[].class);
    }

    private PlayerContribution[] getFactionContributions(QuestFactionContainer container,
                                                         String factionName) {
       Optional<QuestFaction> faction = container.getQuestFaction(factionName);
       return faction.map(QuestFaction::getInvestors).orElse(null);
    }

    private void updateFactionListFile(String s) {
        try {
            Files.write(factionPath, "".getBytes());
            Files.write(factionPath, s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayerContribution[] getContribution(String factionName) {
        return getFactionContributions(getQuestFactions(), factionName);
    }

    public PlayerContribution getContribution(UUID playerUUID) {
        QuestFactionContainer container = getQuestFactions();
        if (container == null) return null;
        Optional<QuestFaction> factions = container.getActiveQuestFaction(playerUUID);
        return factions.map(faction -> faction.getContribution(playerUUID)).orElse(null);
    }

    //--//

    private void updateFaction(QuestFactionContainer container, PlayerContribution contribution, Gson gson) {
        Optional<QuestFaction> faction = container.getActiveQuestFaction(contribution.getPlayer());

        if (faction.isPresent()) {
            faction.get().updateInvestor(contribution);
            container.updateQuestFaction(faction.get());
            updateFactionListFile(gson.toJson(container));
        } else {
            throw new AssertionError("какой то пиздец нахуй поризошел я ебу что ли." +
                    "\nСделай ебучий fetch факций");
        }
    }

    private void appendFaction(QuestFactionContainer container, PlayerContribution contribution, Gson gson) {
        Optional<QuestFaction> faction = container.getQuestFaction(contribution.getFactionName());

        if (faction.isPresent()) {
            faction.get().addInvestor(contribution);
            container.updateQuestFaction(faction.get());
            updateFactionListFile(gson.toJson(container));
        } else {
            throw new AssertionError("какой то пиздец нахуй поризошел я ебу что ли." +
                    "\nСделай ебучий fetch факций");
        }
    }

    private void createFaction(QuestFactionContainer container, PlayerContribution contribution, Gson gson) {
        QuestFaction[] factions = container.getQuestFactions();
        boolean a = true;
        if (factions != null) {
            for (QuestFaction f: factions) {
                if (f.getName().equals(contribution.getFactionName())) {
                    a = false;
                }
            }
        }
        if (a) {
            QuestFaction qFaction = new QuestFaction(contribution.getFactionName());
            qFaction.addInvestor(contribution);
            qFaction.setCurrentLeader(contribution.getPlayer()); // If leader is moved, it breaks
            qFaction.setFactionPower(4);
            container.createQuestFaction(qFaction);
            updateFactionListFile(gson.toJson(container));
        } else {
            throw new AssertionError("какой то пиздец нахуй поризошел я ебу что ли." +
                    "\nСделай ебучий fetch факций");
        }
    }

    /*
    private void renameFaction(QuestFactionContainer container, String factionName, Gson gson) {
        Optional<QuestFaction> faction = container.getQuestFaction(factionName);

        if (faction.isPresent()) {
            //faction.get().setName();
            container.updateQuestFaction(faction.get());
            updateFactionListFile(gson.toJson(container));
        } else {
            throw new AssertionError("какой то пиздец нахуй поризошел я ебу что ли." +
                    "\nСделай ебучий fetch факций");
        }
    }
     */

    private void deleteFaction(QuestFactionContainer container, String factionName, Gson gson) {
        Optional<QuestFaction> faction = container.getQuestFaction(factionName);
        if (faction.isPresent()) {
            container.disbandQuestFaction(faction.get());
            updateFactionListFile(gson.toJson(container));
        } else {
            throw new AssertionError("какой то пиздец нахуй поризошел я ебу что ли." +
                    "\nСделай ебучий fetch факций");
        }
    }

    //--//


    // Contribution > Player because it contains more data we want
    public void updateContribution(PlayerContribution contribution, String mode) {
        String factionName = "";
        PlayerContribution[] contributions = new PlayerContribution[0];

        QuestFactionContainer container = getQuestFactions();
        String newFn = (mode.charAt(0) == 'r') ? mode.substring(1) : null;
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(QuestFactionContainer.class, new QuestFactionContainerSerializer())
                .registerTypeAdapter(QuestFaction.class, new QuestFactionSerializer())
                .registerTypeAdapter(PlayerContribution.class, new PlayerContribtuionSerializer())
                .registerTypeAdapter(Quest.class, new QuestSerializer())
                .create();

        // For clean-up
        if (contribution == null) {
            logger.debug(String.format("Splitting %s", mode));
            factionName = mode.substring(1);
            mode = mode.substring(0, 1);
            logger.debug(String.format("Got %s & %s", factionName, mode));
        }

        logger.debug(String.format("Going into quest switch DataManager %s", mode));

        switch (mode) {
            case "u":
                assert contribution != null;
                updateFaction(container, contribution, gson);
                break;

            case "a":
                assert contribution != null;
                appendFaction(container, contribution, gson);
                break;

            case "c":
                assert contribution != null;
                createFaction(container, contribution, gson);
                break;

            case "r":
                //renameFaction(container, factionName, gson, newFn);
                break;

            case "d":
                deleteFaction(container, factionName, gson);
                break;

            default:
                throw new IllegalArgumentException("Wrong mode argument");
        }
    }

    public void openGUI(Player player, int id) {
        gui.showToPlayer(getContribution(player.getUniqueId()), player, id);
    }

    public void openGUI(Player player, int id, ClickInventoryEvent event) {
        gui.showToPlayer(getContribution(player.getUniqueId()), player, id, event);
    }

    public void closeGUI(Player player, ClickInventoryEvent event) {
        gui.closeGUI(player, event);
    }

    public Quest getQuest(int questLevel, int questId) {
        return questDifficulties[questLevel-1].getQuests()[questId];
    }
}