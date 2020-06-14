package ru.allformine.afmcp.quests;

import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.UUID;

public class QuestFaction {
    private String name;
    private Text tag;
    private int factionPower;
    private UUID currentLeader;
    private PlayerContribution[] investors;

    public QuestFaction(String name, Text tag) {
        this.name = name;
        this.tag = tag;
    }

    public QuestFaction(String name) {
        this.name = name;
        this.tag = EagleFactionsPlugin.getPlugin().getFactionLogic().getFactions().get(name).getTag();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Text getTag() {
        return tag;
    }

    public void setTag(Text tag) {
        this.tag = tag;
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

    public boolean hasInvestor(PlayerContribution investor) {
        return Arrays.stream(investors).anyMatch(x -> x.getPlayer().equals(investor.getPlayer()));
    }

    public boolean hasInvestor(UUID investorUUID) {
        return Arrays.stream(investors).anyMatch(x -> x.getPlayer().equals(investorUUID));
    }

    public PlayerContribution[] getInvestors() {
        return investors;
    }

    public void removeInvestor(PlayerContribution investor) {
        for (PlayerContribution playerContribution : investors) {
            if (playerContribution.getPlayer().equals(investor.getPlayer()))
                playerContribution.setPresent(false);
        }
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

    public void updateInvestor(PlayerContribution investor) {
        for (int i = 0; i < investors.length; i++) {
            if (investors[i].getPlayer().equals(investor.getPlayer())) {
                investors[i] = investor;
                break;
            }
        }
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
}
