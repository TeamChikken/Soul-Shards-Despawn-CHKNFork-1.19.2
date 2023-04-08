package info.x2a.soulshards.compat.jei.categories;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.core.data.MultiblockPattern;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ShardCreationCategory implements IRecipeCategory<ShardCreationCategory.MultiblockWrapper> {
    private static final ResourceLocation CAT_ID = SoulShards.makeResource("multiblock_crafting");
    public static final RecipeType<MultiblockWrapper> RECIPE = new RecipeType<>(CAT_ID, MultiblockWrapper.class);
    private final IDrawable background;
    private final IDrawable icon;
    private static final int WIDTH = 93;
    private static final int HEIGHT = 53;
    private long lastDrawMs = 0;
    private final long switchStateInterval = 1000;
    private long drawTick = 0;
    private static final int CRAFTING_X = 22;
    private static final int CRAFTING_Y = 2;
    private static final int CRAFTING_W = 49;
    private static final int CRAFTING_H = 49;

    public ShardCreationCategory(IGuiHelper gui) {
        this.background = gui.createDrawable(SoulShards.makeResource("gui/soulshardcrafting.png"), 0, 0, WIDTH, HEIGHT);

        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, RegistrarSoulShards.SOUL_SHARD.get()
                                                                                                        .getDefaultInstance());
    }

    @Override
    public @NotNull RecipeType<MultiblockWrapper> getRecipeType() {
        return RECIPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("title.soulshards.soulshard_crafting");
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
        var shape = recipe.pattern.getShape();
        var blockItems = new ArrayList<ItemStack>();
        for (var y = 0; y != shape.length; ++y) {
            for (var x = 0; x != shape[y].length(); ++x) {
                for (var state : recipe.pattern.getSlot(x, y).getStates()) {
                    blockItems.add(state.getBlock().asItem().getDefaultInstance());
                }
            }
        }
        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST)
               .addIngredients(Ingredient.of(blockItems.stream()));
        var HEIGHT_OFFSET = 19;
        builder.addSlot(RecipeIngredientRole.INPUT, 2, HEIGHT_OFFSET)
               .addIngredients(Ingredient.of(recipe.pattern.getCatalyst()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 20, HEIGHT_OFFSET)
               .addIngredient(VanillaTypes.ITEM_STACK, RegistrarSoulShards.SOUL_SHARD.get().getDefaultInstance());
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(MultiblockWrapper recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
        var comps = new ArrayList<Component>();
        comps.add(Component.translatable("desc.soulshards.soulshard_crafting").withStyle(ChatFormatting.DARK_AQUA));
        comps.add(Component.translatable("misc.soulshards.catalyst")
                           .append(": ")
                           .append(recipe.pattern.getCatalyst().getHoverName())
                           .withStyle(ChatFormatting.AQUA));
        if (mouseX > CRAFTING_X && mouseX < WIDTH - CRAFTING_X && mouseY > CRAFTING_Y && mouseY < HEIGHT - CRAFTING_Y) {
            for (var slot : recipe.pattern.getSlots()) {
                var states = slot.getStates();
                var currState = states.get((int) (drawTick % states.size()));
                if (states.size() > 1) {
                    comps.add(Component.translatable("jei.soulshards.oneof"));
                    for (var state : slot.getStates()) {
                        comps.add(Component.literal(" ").append(state.getBlock()
                                                                     .getName()
                                                                     .withStyle(state == currState ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY)));
                    }
                } else {
                    comps.add(currState.getBlock().getName());
                }
            }
        }
        comps.add(Component.translatable("jei.soulshards.consumes_warning")
                           .withStyle(ChatFormatting.RED)
                           .withStyle(ChatFormatting.BOLD));
        return comps;
    }

    @Override
    public void draw(@NotNull MultiblockWrapper recipe, IRecipeSlotsView slots, PoseStack poses, double mouseX, double mouseY) {
        var time = System.currentTimeMillis();
        if (lastDrawMs == 0) {
            lastDrawMs = time;
        }
        if (time - lastDrawMs >= switchStateInterval) {
            lastDrawMs = time;
            ++drawTick;
        }
        var mc = Minecraft.getInstance();
        var shape = recipe.pattern.getShape();
        float baseHeight = shape.length;
        float padding = 0;
        float baseWidth = shape[0].length();
        float scaleX = ((CRAFTING_W - padding * 2) / baseWidth);
        float scaleY = ((CRAFTING_H - padding * 2) / baseHeight);
        var brender = mc.getBlockRenderer();
        var buf = mc.renderBuffers().bufferSource();
        Lighting.setupForFlatItems();
        poses.pushPose();
        poses.translate(CRAFTING_X + padding, CRAFTING_Y + padding, 100);
        poses.scale(scaleX, -scaleY, 1);
        for (var y = 0; y != shape.length; ++y) {
            for (var x = 0; x != shape[y].length(); ++x) {
                poses.pushPose();
                poses.mulPose(new Quaternionf().rotationX((float) (Math.PI / 2F)));
                poses.translate(x, 0, y);
                var states = recipe.pattern.getSlot(x, y)
                                           .getStates();
                brender.renderSingleBlock(states
                        .get((int) (drawTick % states.size())), poses, buf, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                poses.popPose();
            }
        }
        buf.endBatch();
        poses.popPose();
    }

    public static class MultiblockWrapper {
        public final MultiblockPattern pattern;

        public MultiblockWrapper(MultiblockPattern pattern) {
            this.pattern = pattern;
        }
    }
}
