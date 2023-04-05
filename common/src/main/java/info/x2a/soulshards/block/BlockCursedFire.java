package info.x2a.soulshards.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;

public class BlockCursedFire extends SoulFireBlock {
    public BlockCursedFire() {
        super(Properties.copy(Blocks.SOUL_FIRE).color(MaterialColor.COLOR_LIGHT_GRAY));
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        super.entityInside(blockState, level, blockPos, entity);
    }
}
