package ru.allformine.afmcp.jumppad;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class JumpPadEventListener {
    @Listener
    public void onEntityDamage(DamageEntityEvent event, @Root DamageSource source) {
        BlockState block = event.getTargetEntity().getLocation().getExtent().getBlock(event.getTargetEntity().getLocation().getBlockPosition().sub(0, 1, 0));

        if (source.getType() == DamageTypes.FALL) {
            for (JumpPadTypes type : JumpPadTypes.values()) {
                if (type.getBlockState() == block) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @Listener
    public void onEntityMove(MoveEntityEvent event) {
        Location<World> location = event.getTargetEntity()
                .getLocation();
        BlockState block = location.getExtent()
                .getBlock(
                        location.getBlockPosition()
                                .sub(
                                        0,
                                        1,
                                        0
                                )
                );
        if (block == JumpPadTypes.STRAIGHT_UP.getBlockState()) {
            event.getTargetEntity().offer(Keys.VELOCITY, new Vector3d(0, 1, 0));
        } else if (block == JumpPadTypes.PLAYER_LOOK.getBlockState()) {
            double yaw = ((event.getTargetEntity().getRotation().getX() + 90) % 360);
            double pitch = ((event.getTargetEntity().getRotation().getY()) * -1);
            double velX = -1 * Math.sin(yaw / 180 * Math.PI);
            double velZ = Math.cos(yaw / 180 * Math.PI);
            Vector3d vel = new Vector3d(velX, 1, velZ);
            System.out.println(vel.toString());

            event.getTargetEntity().offer(Keys.VELOCITY, vel);
        }
    }
}
