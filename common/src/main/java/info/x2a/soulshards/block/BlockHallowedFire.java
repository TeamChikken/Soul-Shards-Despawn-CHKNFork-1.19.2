package info.x2a.soulshards.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.material.MaterialColor;

public class BlockHallowedFire extends FireBlock {
    public BlockHallowedFire() {
        super(Properties.copy(Blocks.FIRE).color(MaterialColor.GOLD));
    }
}
