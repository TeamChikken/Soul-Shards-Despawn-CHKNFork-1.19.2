package info.x2a.soulshards.compat.jei.categories;

import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.compat.jei.SoulShardsJei;
import info.x2a.soulshards.compat.jei.ingredients.MultiblockIngredient;
import info.x2a.soulshards.core.data.MultiblockPattern;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.EntityBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ShardCreationCategory implements IRecipeCategory<ShardCreationCategory.MultiblockWrapper> {
    private static final ResourceLocation CAT_ID = SoulShards.makeResource("multiblock_crafting");
    public static final RecipeType<MultiblockWrapper> RECIPE = new RecipeType<>(CAT_ID, MultiblockWrapper.class);
    private final IDrawable background;
    private final IDrawable icon;

    public ShardCreationCategory(IGuiHelper gui) {
        this.background = gui.createBlankDrawable(200, 200);
        this.icon = gui.createBlankDrawable(20, 20);
    }

    @Override
    public @NotNull RecipeType<MultiblockWrapper> getRecipeType() {
        return RECIPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("title.soulshards.multiblock_crafting");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull MultiblockWrapper recipe, @NotNull IFocusGroup focuses) {
        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST)
               .addIngredients(Ingredient.of(recipe.pattern.getCatalyst()));
        var shape = recipe.pattern.getShape();
        var z = 0;
        for (var y = 0; y != shape.length; ++y) {
            for (var x = 0; x != shape[y].length(); ++x) {
                int finalX = x;
                int finalY = y;
                var ingredients = recipe.pattern.getSlot(x, y).getStates().stream().map(blockState -> {
                    if (blockState.getBlock() instanceof EntityBlock block) {
                        return new MultiblockIngredient(block.newBlockEntity(new BlockPos(finalX, finalY, z), blockState));
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull).toList();
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                       .addIngredients(SoulShardsJei.MULTIBLOCK_INGREDIENT, ingredients);
            }
        }

    }

    public static class MultiblockWrapper {
        public final MultiblockPattern pattern;

        public MultiblockWrapper(MultiblockPattern pattern) {
            this.pattern = pattern;
        }
    }
}
