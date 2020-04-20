package ru.allformine.afmcp.quests;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import sun.plugin2.main.server.Plugin;


import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
        Gson gson = new GsonBuilder().create();

        // Represent Json as Map
        Type type = new TypeToken<Map<String, PlayerContribution[]>>() {
        }.getType();
        try {
            return gson.fromJson(jsonData, type);
        }
        catch (StackOverflowError e) {
            //// TODO: REMOVE HARDCODED PLUG!!!
            String name = "@dummyFaction";
            Player player = Sponge.getServer().getPlayer("ReDestroyDeR").orElse(null);
            PlayerContribution[] playerContributions = new PlayerContribution[1];
            playerContributions[0] = new PlayerContribution(player,
                    EagleFactionsPlugin.getPlugin().getFactionLogic()
                            .getFactionByPlayerUUID(player.getUniqueId()).orElse(null));

            try {
                Files.write(factionPath, Arrays.toString(playerContributions).getBytes());
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
        // Iterate through keys to find faction by parameter
        for (Map.Entry<String, PlayerContribution[]> entry : map.entrySet()) {
            if (entry.getKey().equals(factionName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void updateFactionListFile(byte[] bytes) {
        try {
            Files.write(factionPath, "".getBytes());
            Files.write(factionPath, bytes);
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
                if (p.getPlayer().getUniqueId().equals(playerUUID) && p.isPresent()) {
                    return p;
                }
            }
        }
        return null;
    }

    // Using player name but not an entity because player is maybe offline
    public void updateContribution(PlayerContribution contribution, String mode) {
        Map<String, PlayerContribution[]> map = getJsonMap();
        String factionName = contribution.getFaction().getName();
        PlayerContribution[] contributions = getFactionContributions(map, factionName);

        assert contributions != null;
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        String newFn = (mode.charAt(0) == 'r') ? mode.substring(1) : null;

        switch (mode) {
            case "u":
                for (int i = 0; i < contributions.length-1; i++) {
                    if (contribution.getPlayer().getName().equals(
                            contributions[i].getPlayer().getName())) {
                        contributions[i] = contribution;
                        break;
                    }
                }

                map.replace(factionName, contributions);
                updateFactionListFile(gson.toJson(map).getBytes());
                break;

            case "a":
                for (PlayerContribution playerContribution : contributions) {
                    if (contribution.getPlayer().getName().equals(
                            playerContribution.getPlayer().getName())) {
                        throw new AssertionError(String.format("Player %s already presents in faction",
                                contribution.getPlayer().getName()));
                    }
                }

                // Append 1 element to array
                PlayerContribution[] playerContributions = new PlayerContribution[contributions.length+1];
                if (playerContributions.length - 2 >= 0)
                    System.arraycopy(contributions, 0, playerContributions, 0, playerContributions.length - 2);
                playerContributions[playerContributions.length-1] = contribution;

                map.replace(factionName, playerContributions);
                updateFactionListFile(gson.toJson(map).getBytes());
                break;

            case "c":
                if (!map.containsKey(factionName)) {
                    PlayerContribution[] pc = new PlayerContribution[1];
                    pc[0] = contribution;
                    map.put(factionName, pc);
                    updateFactionListFile(gson.toJson(map).getBytes());
                } else {
                    throw new AssertionError(String.format("Faction %s with this name has already been registered",
                            factionName));
                }
                break;

            case "r":
                if (!map.containsKey(newFn)) {
                    PlayerContribution[] contributionsR = getFactionContributions(map, factionName);
                    map.remove(factionName);
                    map.put(newFn, contributionsR);
                    updateFactionListFile(gson.toJson(map).getBytes());
                } else {
                    throw new AssertionError(String.format("Faction %s with this name has already been registered",
                            factionName));
                }
                break;

            case "d":
                if (map.containsKey(factionName)) {
                    map.remove(factionName);
                    updateFactionListFile(gson.toJson(map).getBytes());
                } else {
                    throw new AssertionError(String.format("Faction %s does not exist",
                            factionName));
                }
                break;

            default:
                throw new IllegalArgumentException("Wrong mode argument");
        }
    }

    public Quest getQuest(int questLevel, int questId) {
        return questDifficulties.get(questLevel).getQuests()[questId];
    }
}
