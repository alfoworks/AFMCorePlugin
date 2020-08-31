package ru.allformine.afmcp.quests;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class QuestFaction {
    private String name;
    private int factionPower;
    private UUID currentLeader;
    private PlayerContribution[] investors;

    public QuestFaction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFactionPower() {
        return factionPower;
    }

    public void setFactionPower(int factionPower) {
        this.factionPower = factionPower;
    }

    public UUID getCurrentLeader() {
        return currentLeader;
    }

    public void setCurrentLeader(UUID currentLeader) {
        this.currentLeader = currentLeader;
        calculateEnergy();
    }

    public boolean hasInvestor(UUID investorUUID) {
        return Arrays.stream(investors).anyMatch(x -> x.getPlayer().equals(investorUUID));
    }

    public PlayerContribution[] getInvestors() {
        return investors;
    }

    public void addInvestor(PlayerContribution investor) {
        // Restricting hold power
        EagleFactionsPlugin.getPlugin().getPowerManager()
                .setPlayerMaxPower(investor.getPlayer(), 0);

        if (investors != null) {
            investors = Arrays.copyOf(investors, investors.length+1);
            investors[investors.length-1] = investor;
        } else {
            investors = new PlayerContribution[1];
            investors[0] = investor;
        }
    }

    public boolean updateInvestor(PlayerContribution investor) {
        for (int i = 0; i < investors.length; i++) {
            if (investors[i].getPlayer().equals(investor.getPlayer())) {
                if (investors[i].getCompletedQuests().length < investor.getCompletedQuests().length)
                    calculateEnergy();
                investors[i] = investor;
                return true;
            }
        }
        return false;
    }

    /**
     * Doesn't give desired result when QuestFaction investors are empty
     * Loop in EnergyCalculationTask class won't start
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void calculateEnergy() {
        // Creating async task not to freeze server
        Task.builder().execute(new EnergyCalculationTask())
                .async()
                .submit(Sponge.getPluginManager().getPlugin("afmcp").get());
    }

    public void setInvestors(PlayerContribution[] investors) {
        this.investors = investors;
    }

    public PlayerContribution getContribution(UUID player) {
        for (PlayerContribution playerContribution : investors) {
            if (playerContribution.getPlayer().equals(player))
                return playerContribution;
        }
        return null;
    }

    public void removeInvestor(PlayerContribution investor) {
        for (int i = 0; i < investors.length; i++) {
            if (investors[i].getPlayer().equals(investor.getPlayer())) {
                investors[i] = investors[investors.length - 1];
                investors = Arrays.copyOf(investors, investors.length - 1);
            }
        }
    }

    private class EnergyCalculationTask implements Consumer<Task> {
        @Override
        public void accept(Task task) {
            Optional<Faction> faction =
                    EagleFactionsPlugin.getPlugin().getFactionLogic().getFactionByPlayerUUID(currentLeader);
            if (faction.isPresent()) {
                // Restricting vanilla power of players
                for (PlayerContribution contribution : investors) {
                    if (contribution.getPlayer().equals(currentLeader)) {
                        EagleFactionsPlugin.getPlugin().getPowerManager()
                                .setPlayerMaxPower(currentLeader, (investors.length > 6) ? 300 : investors.length * 50);
                    }

                    // Maximum cap per file is 50 energy
                    // 50 divided by All number of quests in file equals contribution by completing 1 quests
                    double impact = 50.0 / contribution.getCompletedQuests().length;
                    double power =
                            0.0
                            + EagleFactionsPlugin.getPlugin().getConfiguration().getPowerConfig().getStartingPower();
                    for (int i = 0; i < contribution.getCompletedQuests().length; i++) {
                        // There can't be any corrupted quests because they're cut off when deserializing from file
                        power += impact;
                    }

                    // Assigning all power to leader
                    EagleFactionsPlugin.getPlugin().getPowerManager()
                            .setPlayerPower(currentLeader, Math.round(power));
                }


            } else {
                throw new NullPointerException("No faction found by leader UUID");
            }
        }
    }
}
