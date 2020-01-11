package ru.alfomine.afmcp.tablist.comparator;

import ru.alfomine.afmcp.PluginConfig;
import ru.alfomine.afmcp.tablist.WrappedTabListEntry;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CompareEntries implements Comparator<WrappedTabListEntry> {

    @Override
    public int compare(WrappedTabListEntry a, WrappedTabListEntry b) {
        String aName = a.permissionUser.getIdentifier();
        String bName = b.permissionUser.getIdentifier();
        List<String> priority = Arrays.asList(PluginConfig.tabSortGroups);
        return Integer.compare(priority.indexOf(aName), priority.indexOf(bName));

    }
}
