package ru.allformine.afmcp.networkwatcher;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.allformine.afmcp.AFMCorePlugin;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

public class NetworkWatcherProxySelector extends ProxySelector {

    private final ProxySelector defaultSelector;

    ProxySelector getDefaultSelector() {
        return defaultSelector;
    }

    NetworkWatcherProxySelector(ProxySelector defaultSelector) {
        this.defaultSelector = defaultSelector;
    }

    @Override
    public List<Proxy> select(URI uri) {
        if (AFMCorePlugin.getPlugin().getConfig().getBoolean("networkwatcher")) {
            Plugin plugin = getRequestingPlugin();
            if (plugin != null) {
                System.out.println("Plugin " + plugin.getName() + " attempted to establish connection " + uri + " in main server thread");
            } else {
                System.out.println("Something attempted to access " + uri + " in main server thread, printing stack trace");
            }
        }
        return defaultSelector.select(uri);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        defaultSelector.connectFailed(uri, sa, ioe);
    }

    private Plugin getRequestingPlugin() {
        HashMap<ClassLoader, Plugin> map = getClassloaderToPluginMap();
        StackTraceElement[] stacktrace = new Exception().getStackTrace();
        for (StackTraceElement element : stacktrace) {
            try {
                ClassLoader loader = Class.forName(element.getClassName(), false, getClass().getClassLoader()).getClassLoader();
                if (map.containsKey(loader)) {
                    return map.get(loader);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private HashMap<ClassLoader, Plugin> getClassloaderToPluginMap() {
        HashMap<ClassLoader, Plugin> map = new HashMap<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            map.put(plugin.getClass().getClassLoader(), plugin);
        }
        map.remove(getClass().getClassLoader());
        return map;
    }

}