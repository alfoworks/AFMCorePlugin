package ru.allformine.afmcp.networkwatcher;

import ru.allformine.afmcp.AFMCorePlugin;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

public class NetworkWatcherProxySelector extends ProxySelector {
    private final ProxySelector defaultSelector;

    public ProxySelector getDefaultSelector() {
        return defaultSelector;
    }

    public NetworkWatcherProxySelector(ProxySelector defaultSelector) {
        this.defaultSelector = defaultSelector;
    }

    @Override
    public List<Proxy> select(URI uri) {
        if (AFMCorePlugin.getPlugin().getConfig().getBoolean("litebans_crack") && uri.toString().contains("litebans")) {
            System.out.println("[LiteBans Crack] Plugin has attempted to validate its license.");
        }

        return defaultSelector.select(uri);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        defaultSelector.connectFailed(uri, sa, ioe);
    }
}