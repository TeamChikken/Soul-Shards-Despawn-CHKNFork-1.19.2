package info.x2a.soulshards.compat.jei.ingredients;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MultiblockIngredient {
    public final BlockEntity entity;

    public MultiblockIngredient(BlockEntity entity) {
        this.entity = entity;
    }


    public String getDisplayName() {
        return entity.getBlockState().getBlock().getName().toString();
    }
}
