package ru.iterator.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class LocationUtil {
    public static String toString(Location location) {
        List<String> locationList = new ArrayList<>();

        locationList.add(String.valueOf(location.getBlockX()));
        locationList.add(String.valueOf(location.getBlockY()));
        locationList.add(String.valueOf(location.getBlockZ()));
        locationList.add(location.getWorld().getName());

        return String.join(",", locationList);
    }

    public static Location fromString(String locationString) {
        String[] locationArray = locationString.split(",");

        return new Location(Bukkit.getWorld(locationArray[3]), Double.parseDouble(locationArray[0]), Double.parseDouble(locationArray[1]), Double.parseDouble(locationArray[2]));
    }
}
