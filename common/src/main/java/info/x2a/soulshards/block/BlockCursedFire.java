package info.x2a.soulshards.block;

import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class BlockCursedFire extends SoulFireBlock {
    public BlockCursedFire() {
        super(Properties.copy(Blocks.SOUL_FIRE).mapColor(MapColor.COLOR_LIGHT_GRAY));
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (entity instanceof ItemEntity ent) {
            var recipes = level.getRecipeManager()
                               .getAllRecipesFor(RegistrarSoulShards.CURSING_RECIPE.get())
                               .stream()
                               .filter(r -> r.input() == ent.getItem().getItem())
                               .toList();
            if (!recipes.isEmpty()) {
                var recipeIdx = 0;
                var stack = ent.getItem();
                while (!stack.isEmpty()) {
                    var recipe = recipes.get(recipeIdx);
                    if (recipeIdx == recipes.size() - 1) {
                        recipeIdx = 0;
                    } else {
                        ++recipeIdx;
                    }

                    stack.shrink(1);
                    var newStack = recipe.getResult();

                    var newEnt = new ItemEntity(level, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), newStack);
                    newEnt.setDeltaMovement(entity.getDeltaMovement().reverse());
                    level.addFreshEntity(newEnt);
                }
                return;
            }
            super.entityInside(blockState, level, blockPos, entity);
        }
    }
}
