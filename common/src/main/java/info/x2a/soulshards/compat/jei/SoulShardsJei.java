package info.x2a.soulshards.compat.jei;

import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.compat.jei.categories.ShardCreationCategory;
import info.x2a.soulshards.compat.jei.ingredients.MultiblockHelper;
import info.x2a.soulshards.compat.jei.ingredients.MultiblockIngredient;
import info.x2a.soulshards.compat.jei.ingredients.MultiblockRenderer;
import info.x2a.soulshards.core.config.ConfigServer;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SoulShardsJei implements IModPlugin {
    public static final IIngredientType<MultiblockIngredient> MULTIBLOCK_INGREDIENT = () -> MultiblockIngredient.class;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return SoulShards.makeResource("jei_plugin");
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(MULTIBLOCK_INGREDIENT, new ArrayList<>(), new MultiblockHelper(), new MultiblockRenderer());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(RegistrarSoulShards.SOUL_SHARD.get());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IModPlugin.super.registerCategories(registration);
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new ShardCreationCategory(gui));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IModPlugin.super.registerRecipes(registration);
        registration.addRecipes(ShardCreationCategory.RECIPE, List.of(new ShardCreationCategory.MultiblockWrapper(ConfigServer.getMultiblock())));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ConfigServer.getMultiblock().getCatalyst(), ShardCreationCategory.RECIPE);
    }
}
