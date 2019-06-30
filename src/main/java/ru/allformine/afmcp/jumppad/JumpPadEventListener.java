package ru.allformine.afmcp.jumppad;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class JumpPadEventListener {
    @Listener
    public void onEntityDamage(DamageEntityEvent event, @Root DamageSource source) {
        BlockState block = event.getTargetEntity().getLocation().getExtent().getBlock(event.getTargetEntity().getLocation().getBlockPosition().sub(0, 1, 0));

        if (source.getType() == DamageTypes.FALL && block == BlockTypes.GOLD_BLOCK.getDefaultState()) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onEntityMove(MoveEntityEvent event) {
        BlockState block = event.getTargetEntity().getLocation().getExtent().getBlock(event.getTargetEntity().getLocation().getBlockPosition().sub(0, 1, 0));

        if (block == BlockTypes.GOLD_BLOCK.getDefaultState()) {
            event.getTargetEntity().offer(Keys.VELOCITY, new Vector3d(0, 1, 0));
        }
    }
}
