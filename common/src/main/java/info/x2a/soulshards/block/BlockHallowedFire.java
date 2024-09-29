package info.x2a.soulshards.block;

import info.x2a.soulshards.SoulShards;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlockHallowedFire extends FireBlock {
    @Nullable
    private final Method canBurnRefl;

    public BlockHallowedFire() {
        super(Properties.copy(Blocks.FIRE).mapColor(MapColor.GOLD));
        Method canBurn = null;
        try {
            canBurn = ((FireBlock) Blocks.FIRE).getClass().getDeclaredMethod("canBurn", BlockState.class);
            canBurn.setAccessible(true);
        } catch (Exception e) {
            SoulShards.Log.error(e);
        }
        canBurnRefl = canBurn;
    }

    @Override
    protected boolean canBurn(BlockState st) {
        if (canBurnRefl == null) {
            return false;
        }
        try {
            return (boolean) canBurnRefl.invoke(Blocks.FIRE, st);
        } catch (Exception e) {
            SoulShards.Log.error(e);
            return false;
        }
    }

    public BlockState getStateForFace(Level world, BlockPos pos) {
        return getStateForPlacement(world, pos);
    }
}
