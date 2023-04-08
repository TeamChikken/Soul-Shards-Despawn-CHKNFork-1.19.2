package info.x2a.soulshards.compat.jei.categories;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.compat.jei.RecipeBackground;
import info.x2a.soulshards.compat.jei.SoulShardsJei;
import info.x2a.soulshards.core.recipe.CursingRecipe;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CursingCategory implements IRecipeCategory<CursingRecipe> {

    public static final ResourceLocation CAT_ID = SoulShards.makeResource("jei_cursing");
    public static final RecipeType<CursingRecipe> RECIPE = new RecipeType<>(CAT_ID, CursingRecipe.class);
    @NotNull
    private final IDrawable icon;
    @NotNull
    private final IDrawable background;

    private static final RecipeBackground UI = SoulShardsJei.DEFAULT_BG;


    public CursingCategory(IGuiHelper gui) {
        this.background = UI.background(gui);

        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, Items.SOUL_SAND.getDefaultInstance());
    }

    @Override
    public @NotNull RecipeType<CursingRecipe> getRecipeType() {
        return RECIPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("title.soulshards.cursing");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CursingRecipe recipe, IFocusGroup focuses) {
        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST)
               .addIngredients(Ingredient.of(Items.SOUL_SAND.getDefaultInstance().getItem().getDefaultInstance()));
        builder.addSlot(RecipeIngredientRole.INPUT, (int) UI.inputPositions[0].x, (int) UI.inputPositions[0].y)
               .addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, (int) UI.outputPositions[0].x, (int) UI.outputPositions[0].y)
               .addIngredients(Ingredient.of(recipe.getResult()));
    }

    @Override
    public void draw(CursingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {

        stack.pushPose();
        stack.translate(UI.craftXOffset + UI.craftWidth / 2.0F - (UI.craftHeight / 4F), UI.craftYOffset, 100);
        UI.scaleForCrafting(stack, 1, 2);
        var mc = Minecraft.getInstance();
        var brender = mc.getBlockRenderer();
        var buf = mc.renderBuffers().bufferSource();
        Lighting.setupForFlatItems();
        stack.pushPose();
        stack.translate(0, -1.5, 0);
        brender.renderSingleBlock(RegistrarSoulShards.CURSED_FIRE.get()
                                                                 .defaultBlockState(), stack, buf, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        stack.popPose();
        stack.pushPose();
        stack.translate(0, -2, 0);
        brender.renderSingleBlock(Blocks.SOUL_SAND.defaultBlockState(), stack, buf, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        stack.popPose();
        buf.endBatch();
        stack.popPose();
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(CursingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        var comps = new ArrayList<Component>();
        comps.add(Component.translatable("desc.soulshards.cursing").withStyle(ChatFormatting.DARK_AQUA));
        if (UI.inCraftArea((int) mouseX, (int) mouseY)) {
            comps.add(RegistrarSoulShards.CURSED_FIRE.get().getName());
            comps.add(Blocks.SOUL_SAND.getName());
        }
        return comps;
    }
}
