package ru.allformine.afmcp.packet;

import org.bukkit.entity.Player;
import ru.allformine.afmcp.AFMCorePlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TerritoryShow {

    public static void sendTSPacketToPlayer(String name, Player player) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF(name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(AFMCorePlugin.getPlugin(), "territoryshow", b.toByteArray());
    }
}
