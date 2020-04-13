package ru.allformine.afmcp;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.allformine.afmcp.net.api.Broadcast;

import java.util.ArrayList;

public class BroadcastTask implements Runnable {
    public ArrayList<String> broadcasts;
    private int count;

    public BroadcastTask(ArrayList<String> broadcasts) {
        this.broadcasts = broadcasts;
    }

    public void updateBroadcasts(ArrayList<String> broadcasts) {
        this.broadcasts = broadcasts;
        this.count = 0;
    }

    @Override
    public void run() {
        if (Broadcast.broadcastPrefix == null) {
            AFMCorePlugin.logger.error("No broadcast prefix available!");
            return;
        }

        Text text = Text.builder()
                .append(TextSerializers.FORMATTING_CODE.deserialize(Broadcast.broadcastPrefix))
                .append(Text.of(" "))
                .append(TextSerializers.FORMATTING_CODE.deserialize(broadcasts.get(count)))
                .build();

        Sponge.getServer().getBroadcastChannel().send(text);

        count++;

        if (count == broadcasts.size()) {
            count = 0;
        }
    }
}
