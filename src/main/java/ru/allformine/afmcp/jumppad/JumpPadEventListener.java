package ru.allformine.afmcp.jumppad;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ru.allformine.afmcp.Utils;

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
        Entity entity = event.getTargetEntity();
        Vector3d entityVel = entity.getVelocity();

        if (entity instanceof Player) {
            if (Utils.isSneaking((Player) entity)) {
                return;
            }
        }

        Location<World> location = entity.getLocation();
        BlockState block = location.getExtent()
                .getBlock(location.getBlockPosition()
                        .sub(0, 1, 0)
                );

        if (block == JumpPadTypes.STRAIGHT_UP.getBlockState()) {
            Vector3d jumpVel = new Vector3d(0, 1, 0);
            event.getTargetEntity().offer(Keys.VELOCITY, jumpVel);
        } else if (block == JumpPadTypes.PLAYER_LOOK.getBlockState()) {
            double yaw = event.getTargetEntity().getRotation().getY() + 180;
            double velX = Math.sin(Math.toRadians(yaw));
            double velZ = -1 * Math.cos(Math.toRadians(yaw));

            Vector3d jumpVel = new Vector3d(velX * 1.5, 1, velZ * 1.5).add(entityVel);
            event.getTargetEntity().offer(Keys.VELOCITY, jumpVel);
        }
    }
}
