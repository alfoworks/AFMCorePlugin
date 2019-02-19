package ru.allformine.afmcp.packet;

import org.bukkit.entity.Player;
import ru.allformine.afmcp.AFMCorePlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Ambient {

    public static void sendAmbientMusicPacket(Boolean stop, Player player, String url) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeByte(stop ? 2 : 1);
            out.writeUTF(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(AFMCorePlugin.getPlugin(), "ambient", b.toByteArray());
    }
}
