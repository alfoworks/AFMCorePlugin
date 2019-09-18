package ru.allformine.afmcp.jumppad;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;

enum JumpPadTypes {
    STRAIGHT_UP(BlockTypes.GOLD_BLOCK.getDefaultState()),
    PLAYER_LOOK(BlockTypes.EMERALD_BLOCK.getDefaultState());

    private BlockState block;

    public BlockState getBlockState() {
        return this.block;
    }

    private JumpPadTypes(BlockState block) {
        this.block = block;
    }
}
