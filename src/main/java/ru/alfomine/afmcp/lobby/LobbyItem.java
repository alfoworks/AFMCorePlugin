package ru.alfomine.afmcp.lobby;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LobbyItem {
    public String name;
    public Material material;
    public int id;
    private LobbyItemClick click;

    public LobbyItem(String name, Material material, int id, LobbyItemClick click) {
        this.name = name;
        this.material = material;
        this.id = id;
        this.click = click;
    }

    public void onClick(Player player) {
        click.onClick(player);
    }

    public interface LobbyItemClick {
        void onClick(Player player);
    }
}
