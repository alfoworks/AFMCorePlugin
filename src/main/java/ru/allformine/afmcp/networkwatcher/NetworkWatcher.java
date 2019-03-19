package ru.allformine.afmcp.networkwatcher;

import java.net.ProxySelector;

public class NetworkWatcher {
    public boolean registered = false;

    public void register() {
        ProxySelector.setDefault(new NetworkWatcherProxySelector(ProxySelector.getDefault()));

        registered = true;
    }

    public void unregister() {
        ProxySelector cur = ProxySelector.getDefault();
        if (cur instanceof NetworkWatcherProxySelector) {
            ProxySelector.setDefault(((NetworkWatcherProxySelector) cur).getDefaultSelector());

            registered = false;
        }
    }
}