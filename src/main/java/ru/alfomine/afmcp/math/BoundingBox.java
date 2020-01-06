package ru.alfomine.afmcp.math;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class BoundingBox {

    //min and max points of hit box
    Vector max;
    Vector min;

    BoundingBox(Vector min, Vector max) {
        this.max = max;
        this.min = min;
    }


    BoundingBox(Block block) {
        net.minecraft.server.v1_12_R1.BlockPosition bp = new net.minecraft.server.v1_12_R1.BlockPosition(block.getX(), block.getY(), block.getZ());
        net.minecraft.server.v1_12_R1.WorldServer world = ((org.bukkit.craftbukkit.v1_12_R1.CraftWorld) block.getWorld()).getHandle();
        net.minecraft.server.v1_12_R1.IBlockData blockData = (net.minecraft.server.v1_12_R1.IBlockData) (world.getType(bp));
        net.minecraft.server.v1_12_R1.Block blockNative = blockData.getBlock();
        net.minecraft.server.v1_12_R1.AxisAlignedBB aabb = blockNative.a(blockData, new IBlockAccess() {
            @Nullable
            @Override
            public TileEntity getTileEntity(BlockPosition blockPosition) {
                return null;
            }

            @Override
            public IBlockData getType(BlockPosition blockPosition) {
                return null;
            }

            @Override
            public boolean isEmpty(BlockPosition blockPosition) {
                return false;
            }

            @Override
            public int getBlockPower(BlockPosition blockPosition, EnumDirection enumDirection) {
                return 0;
            }
        }, bp);
        min = new Vector(aabb.a, aabb.b, aabb.c);
        max = new Vector(aabb.d, aabb.e, aabb.f);
    }

    //gets min and max point of entity
    // only certain nms versions ****
//    BoundingBox(Entity entity){
//        AxisAlignedBB bb = ((CraftEntity) entity).getHandle().getBoundingBox();
//        min = new Vector(bb.a,bb.b,bb.c);
//        max = new Vector(bb.d,bb.e,bb.f);
//    }

    BoundingBox(AxisAlignedBB bb) {
        min = new Vector(bb.a, bb.b, bb.c);
        max = new Vector(bb.d, bb.e, bb.f);
    }

    public Vector midPoint() {
        return max.clone().add(min).multiply(0.5);
    }

}
