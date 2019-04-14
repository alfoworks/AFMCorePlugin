package ru.allformine.afmcp;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "afmcoreplugin",
        name = "AFMCorePlugin",
        description = "A plugin for a couple of random tasks.",
        url = "https://allformine.ru",
        authors = {
                "Iterator"
        }
)
public class AFMCorePlugin {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {

    }
}
