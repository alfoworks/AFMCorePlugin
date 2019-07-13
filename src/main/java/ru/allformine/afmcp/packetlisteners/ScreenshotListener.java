package ru.allformine.afmcp.packetlisteners;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.api.Platform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.serverapi.HTTPServer;

import static java.lang.Math.min;

public class ScreenshotListener implements RawDataListener {
    @Override
    public void handlePayload(ChannelBuf buf, RemoteConnection connection, Platform.Type side) {
        if (!(connection instanceof PlayerConnection)) {
            return;
        }

        Player player = ((PlayerConnection) connection).getPlayer();

        HTTPServer apiServer = AFMCorePlugin.apiServer;

        if (apiServer.playerScreenshotConfirmation.get(player) == null) {
            return;
        }

        boolean isEnd = buf.readBoolean();

        if (isEnd) {
            apiServer.playerScreenshotConfirmation.replace(player, true);
        } else {
            byte[] chunkByteArray = buf.readBytes(min(10240, buf.available()));
            byte[] prevArr = apiServer.playerScreenshotData.get(player);

            apiServer.playerScreenshotData.replace(player, ArrayUtils.addAll(prevArr, chunkByteArray));
        }
    }
}
