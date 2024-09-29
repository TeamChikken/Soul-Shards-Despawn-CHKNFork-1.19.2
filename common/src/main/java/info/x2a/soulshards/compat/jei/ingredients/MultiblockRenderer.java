package info.x2a.soulshards.compat.jei.ingredients;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiblockRenderer implements IIngredientRenderer<MultiblockIngredient> {
    @Override
    public void render(@NotNull GuiGraphics gui, @NotNull MultiblockIngredient ingredient) {
        var stack = gui.pose();
        stack.pushPose();
        var game = Minecraft.getInstance();
        var renderer = game.getBlockEntityRenderDispatcher();
        var buf = game.renderBuffers().bufferSource();
        renderer.render(ingredient.entity, 0, stack, buf);
        buf.endBatch();
        stack.popPose();
    }

    @Override
    // I know its deprecated but its still required because java
    public List<Component> getTooltip(MultiblockIngredient ingredient, TooltipFlag tooltipFlag) {
        return List.of(Component.literal(ingredient.getDisplayName()));
    }

    @Override
    public void getTooltip(@NotNull ITooltipBuilder builder, @NotNull MultiblockIngredient ingredient, @NotNull TooltipFlag tooltipFlag) {
        builder.add(Component.literal(ingredient.getDisplayName()));
    }
}
