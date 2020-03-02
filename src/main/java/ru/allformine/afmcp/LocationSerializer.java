package ru.allformine.afmcp;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class LocationSerializer {
    public Location<World> deserialize(ConfigurationNode value) {
        String name = value.getNode("worldName").getString();

        if (name == null || !Sponge.getServer().getWorld(name).isPresent()) {
            return null;
        }

        double x, y, z;
        ConfigurationNode nodeX, nodeY, nodeZ;

        nodeX = value.getNode("x");
        nodeY = value.getNode("y");
        nodeZ = value.getNode("z");

        x = nodeX.getDouble();
        y = nodeY.getDouble();
        z = nodeZ.getDouble();

        World w = Sponge.getServer().getWorld(name).get();
        return w.getLocation(x, y, z);
    }

    public void serialize(Location<World> loc, ConfigurationNode value) {
        value.getNode("worldName").setValue(Sponge.getServer().getWorld(loc.createSnapshot().getWorldUniqueId()).get().getName());
        value.getNode("x").setValue(loc.getX());
        value.getNode("y").setValue(loc.getY());
        value.getNode("z").setValue(loc.getZ());
    }
}
