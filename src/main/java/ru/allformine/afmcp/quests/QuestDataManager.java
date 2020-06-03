package ru.allformine.afmcp.quests;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import ru.allformine.afmcp.AFMCorePlugin;


import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class QuestDataManager {
    private Logger logger = AFMCorePlugin.logger;

    private List<QuestLevel> questDifficulties;
    private Object factionContributions;
    private final Path factionPath;
    private final QuestGUI gui;

    // Constructs both data files
    public QuestDataManager(Path questsPath, Path factionsPath) {
        this.factionPath = factionsPath;
        this.gui = new QuestGUI();
        List<QuestLevel> qll = new ArrayList<>();
        Map<String, Quest[]> levels = getJsonMap(questsPath);
        for (Map.Entry<String, Quest[]> e : levels.entrySet()) {
            QuestLevel ql = new QuestLevel();
            ql.setQuests(e.getValue());
            qll.add(ql);
        }
        this.questDifficulties = qll;
    }

    private Map<String, PlayerContribution[]> getJsonMap() {
        String jsonData = null;
        try {
            jsonData = new String(Files.readAllBytes(factionPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Type type = new TypeToken<Map<String, PlayerContribution[]>>() {
        }.getType();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(type, new FactionDeserializer())
                .create();

        // Represent Json as Map

        try {
            Map<String, PlayerContribution[]> temp = gson.fromJson(jsonData, type);
            if (temp == null) throw new NullPointerException();
            else return temp;
        } catch (NullPointerException e) {
            try {
                Map<String, PlayerContribution[]> map = new HashMap<>();
                map.put("", null);
                Files.write(factionPath, gson.toJson(map).getBytes());
                jsonData = new String(Files.readAllBytes(factionPath));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            return gson.fromJson(jsonData, type);
        }
    }

    private Map<String, Quest[]> getJsonMap(Path path) {
        String jsonData = null;
        try {
            jsonData = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().create();

        // Represent Json as Map
        Type type = new TypeToken<Map<String, Quest[]>>() {
        }.getType();

        return gson.fromJson(jsonData, type);
    }

    private PlayerContribution[] getFactionContributions(Map<String, PlayerContribution[]> map,
                                                         String factionName) {
        try {
            // Iterate through keys to find faction by parameter
            for (Map.Entry<String, PlayerContribution[]> entry : map.entrySet()) {
                if (entry.getKey().equals(factionName)) {
                    return entry.getValue();
                }
            }
        } catch (NullPointerException e) {
            logger.warn("Couldn't find faction.\n" +
                    "Maybe it hasn't been registered");
        }
        return null;
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
        Map<String, PlayerContribution[]> map = getJsonMap();
        return getFactionContributions(map, factionName);
    }

    public PlayerContribution getContribution(UUID playerUUID) {
        Map<String, PlayerContribution[]> map = getJsonMap();
        for (Map.Entry<String, PlayerContribution[]> e : map.entrySet()) {
            for (PlayerContribution p : e.getValue()) {
                if (p.getPlayer().equals(playerUUID) && p.isPresent()) {
                    return p;
                }
            }
        }
        return null;
    }

    //--//

    private void updateFaction(@NotNull PlayerContribution[] contributions, PlayerContribution contribution,
                               Map<String, PlayerContribution[]> map, String factionName, Gson gson) {
        for (int i = 0; i < contributions.length; i++) {
            if (contribution.getPlayer().equals(
                    contributions[i].getPlayer())) {
                contributions[i] = contribution;
                logger.debug("Found player - " + contribution.getPlayer());
                break;
            } else {
                logger.debug("X player - " + contribution.getPlayer());
            }
        }

        map.replace(factionName, contributions);
        updateFactionListFile(gson.toJson(map));
        logger.error("Finished quest FACTION UPDATE");
    }

    private void appendFaction(@NotNull PlayerContribution[] contributions, PlayerContribution contribution,
                               Map<String, PlayerContribution[]> map, String factionName, Gson gson) {
        for (PlayerContribution playerContribution : contributions) {
            if (contribution.getPlayer().equals(
                    playerContribution.getPlayer())) {
                throw new AssertionError(String.format("Player %s already presents in faction",
                        contribution.getPlayer()));
            }
        }

        // Append 1 element to array
        PlayerContribution[] playerContributions = new PlayerContribution[contributions.length + 1];
        System.arraycopy(contributions, 0, playerContributions, 0, contributions.length);
        playerContributions[contributions.length] = contribution;

        playerContributions[playerContributions.length - 1] = contribution;
        map.replace(factionName, playerContributions);
        updateFactionListFile(gson.toJson(map));
        logger.debug("Finished quest FACTION APPEND");
    }

    private void popFaction(@NotNull PlayerContribution[] contributions, PlayerContribution contribution,
                               Map<String, PlayerContribution[]> map, String factionName, Gson gson) {
        boolean test = false;
        for (PlayerContribution playerContribution : contributions) {
            if (contribution.getPlayer().equals(playerContribution.getPlayer())) {
                    test = true;
                    break;
            }
        }
        if (!test)
            throw new AssertionError(String.format("Player %s is not in faction",
                    contribution.getPlayer()));

        // Popping 1 element from array
        PlayerContribution[] playerContributions = new PlayerContribution[contributions.length - 1];
        for (int i = 0; i < contributions.length; i++) {
            if (!contributions[i].getPlayer().equals(contribution.getPlayer())) {
                playerContributions[i] = contributions[i];
            }
        }

        map.replace(factionName, playerContributions);
        updateFactionListFile(gson.toJson(map));
        logger.debug("Finished quest FACTION APPEND");
    }

    private void createFaction(@NotNull Map<String, PlayerContribution[]> map, String factionName, Gson gson,
                               PlayerContribution contribution) {
        if (!map.containsKey(factionName)) {
            PlayerContribution[] pc = new PlayerContribution[1];
            pc[0] = contribution;
            map.put(factionName, pc);
            updateFactionListFile(gson.toJson(map));
            logger.debug("Finished quest FACTION CREATE");
        } else {
            throw new AssertionError(String.format("Faction %s with this name has already been registered",
                    factionName));
        }
    }

    private void renameFaction(@NotNull Map<String, PlayerContribution[]> map, String factionName, Gson gson,
                               String newFn) {
        if (!map.containsKey(newFn)) {
            PlayerContribution[] contributionsR = getFactionContributions(map, factionName);
            map.remove(factionName);
            map.put(newFn, contributionsR);
            updateFactionListFile(gson.toJson(map));
            logger.debug("Finished quest FACTION RENAME");
        } else {
            throw new AssertionError(String.format("Faction %s with this name has already been registered",
                    factionName));
        }
    }

    private void deleteFaction(@NotNull Map<String, PlayerContribution[]> map, String factionName, Gson gson) {
        map.entrySet().removeIf(e -> e.getKey().equals(factionName));
        map.keySet().remove(factionName);
        updateFactionListFile(gson.toJson(map));
        logger.debug("Finished quest FACTION DISBAND");
    }

    //--//


    // Contribution > Player because it contains more data we want
    public void updateContribution(PlayerContribution contribution, String mode) {
        String factionName;
        PlayerContribution[] contributions = new PlayerContribution[0];

        Map<String, PlayerContribution[]> map = getJsonMap();
        String newFn = (mode.charAt(0) == 'r') ? mode.substring(1) : null;
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(PlayerContribution.class, new FactionSerializer())
                .create();

        if (contribution != null) {
            factionName = contribution.getFactionName();
            contributions = getFactionContributions(map, factionName);

            assert contributions != null;
            assert contribution.getPlayer() != null;
        } else {
            logger.debug(String.format("Splitting %s", mode));
            factionName = mode.substring(1);
            mode = mode.substring(0, 1);
            logger.debug(String.format("Got %s & %s", factionName, mode));
        }

        logger.debug(String.format("Going into quest switch DataManager %s", mode));
        logger.debug(map.toString());

        switch (mode) {
            case "u":
                updateFaction(contributions, contribution, map, factionName, gson);
                break;

            case "p":
                popFaction(contributions, contribution, map, factionName, gson);
                break;

            case "a":
                appendFaction(contributions, contribution, map, factionName, gson);
                break;

            case "c":
                createFaction(map, factionName, gson, contribution);
                break;

            case "r":
                renameFaction(map, factionName, gson, newFn);
                break;

            case "d":
                deleteFaction(map, factionName, gson);
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
        return questDifficulties.get(questLevel-1).getQuests()[questId];
    }
}
