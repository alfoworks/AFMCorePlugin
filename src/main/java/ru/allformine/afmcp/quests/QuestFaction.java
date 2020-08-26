package ru.allformine.afmcp.quests;

import java.util.Arrays;
import java.util.UUID;

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
    }

    public boolean hasInvestor(UUID investorUUID) {
        return Arrays.stream(investors).anyMatch(x -> x.getPlayer().equals(investorUUID));
    }

    public PlayerContribution[] getInvestors() {
        return investors;
    }

    public void addInvestor(PlayerContribution investor) {
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
                investors[i] = investor;
                return true;
            }
        }
        return false;
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
}
