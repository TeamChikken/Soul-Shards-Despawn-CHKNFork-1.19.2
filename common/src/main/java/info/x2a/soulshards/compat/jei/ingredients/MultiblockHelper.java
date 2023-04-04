package info.x2a.soulshards.compat.jei.ingredients;

import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.compat.jei.SoulShardsJei;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MultiblockHelper implements IIngredientHelper<MultiblockIngredient> {

    @Override
    public @NotNull IIngredientType<MultiblockIngredient> getIngredientType() {
        return SoulShardsJei.MULTIBLOCK_INGREDIENT;
    }

    @Override
    public @NotNull String getDisplayName(MultiblockIngredient ingredient) {
        return ingredient.getDisplayName();
    }

    @Override
    public @NotNull String getUniqueId(@NotNull MultiblockIngredient ingredient, @NotNull UidContext context) {
        try {
            return SoulShards.MODID + BlockEntityType.getKey(ingredient.entity.getType());
        } catch (Exception e) {
            return "soulshards:invalid_id";
        }
    }

    @Override
    public @NotNull ResourceLocation getResourceLocation(@NotNull MultiblockIngredient ingredient) {
        return Objects.requireNonNull(Registry.BLOCK_ENTITY_TYPE.getKey(ingredient.entity.getType()));
    }

    @Override
    public @NotNull MultiblockIngredient copyIngredient(@NotNull MultiblockIngredient ingredient) {
        return ingredient;
    }

    @Override
    public @NotNull String getErrorInfo(@Nullable MultiblockIngredient ingredient) {
        if (ingredient == null) {
            return "null recipe";
        } else {
            return ingredient.entity.toString();
        }
    }
}
