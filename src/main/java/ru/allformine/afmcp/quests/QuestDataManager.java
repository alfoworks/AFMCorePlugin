package ru.allformine.afmcp.quests;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.media.jfxmedia.logging.Logger;
import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;


import javax.sound.midi.SysexMessage;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class QuestDataManager {
    private List<QuestLevel> questDifficulties;
    private Object factionContributions;
    private final Path factionPath;

    // Constructs both data files
    public QuestDataManager(Path questsPath, Path factionsPath) {
        this.factionPath = factionsPath;
        List<QuestLevel> qll = new ArrayList<>();
        Map<String, Quest[]> levels = getJsonMap(questsPath);
        for (Map.Entry<String, Quest[]> e: levels.entrySet()) {
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Represent Json as Map
        Type type = new TypeToken<Map<String, PlayerContribution[]>>() {
        }.getType();
        try {
            return gson.fromJson(jsonData, type);
        }
        catch (NullPointerException e) {
            try {
                Map<String, PlayerContribution[]> map = new HashMap<>();
                map.put(null, null);
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
        }
        catch (NullPointerException e) {
            Logger.logMsg(Logger.WARNING, "Couldn't find faction.\n" +
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
        for (Map.Entry<String, PlayerContribution[]> e: map.entrySet()) {
            for (PlayerContribution p: e.getValue()) {
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
        for (int i = 0; i < contributions.length-1; i++) {
            if (contribution.getPlayer().equals(
                    contributions[i].getPlayer())) {
                contributions[i] = contribution;
                break;
            }
        }

        map.replace(factionName, contributions);
        updateFactionListFile(gson.toJson(map));
        Logger.logMsg(com.sun.media.jfxmedia.logging.Logger.DEBUG,
                "Finished quest FACTION UPDATE");
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
        PlayerContribution[] playerContributions = new PlayerContribution[contributions.length+1];
        if (playerContributions.length - 2 >= 0)
            System.arraycopy(contributions, 0, playerContributions, 0, playerContributions.length - 2);
        playerContributions[playerContributions.length-1] = contribution;

        map.replace(factionName, playerContributions);
        updateFactionListFile(gson.toJson(map));
        Logger.logMsg(com.sun.media.jfxmedia.logging.Logger.DEBUG,
                "Finished quest FACTION APPEND");
    }

    private void createFaction(@NotNull Map<String, PlayerContribution[]> map, String factionName, Gson gson,
                               PlayerContribution contribution) {
        if (!map.containsKey(factionName)) {
            PlayerContribution[] pc = new PlayerContribution[1];
            pc[0] = contribution;
            map.put(factionName, pc);
            updateFactionListFile(gson.toJson(map));
            Logger.logMsg(com.sun.media.jfxmedia.logging.Logger.DEBUG,
                    "Finished quest FACTION CREATE");
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
            Logger.logMsg(com.sun.media.jfxmedia.logging.Logger.DEBUG,
                    "Finished quest FACTION RENAME");
        } else {
            throw new AssertionError(String.format("Faction %s with this name has already been registered",
                    factionName));
        }
    }

    private void deleteFaction(@NotNull Map<String, PlayerContribution[]> map, String factionName, Gson gson) {
        if (map.containsKey(factionName)) {
            map.remove(factionName);
            updateFactionListFile(gson.toJson(map));
            Logger.logMsg(com.sun.media.jfxmedia.logging.Logger.DEBUG,
                    "Finished quest FACTION DISBAND");
        } else {
            throw new AssertionError(String.format("Faction %s does not exist",
                    factionName));
        }
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
            factionName = mode.substring(1);
            mode = mode.substring(0, 0);
        }

        switch (mode) {
            case "u":
                updateFaction(contributions, contribution, map, factionName, gson);
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

    public Quest getQuest(int questLevel, int questId) {
        return questDifficulties.get(questLevel).getQuests()[questId];
    }
}
