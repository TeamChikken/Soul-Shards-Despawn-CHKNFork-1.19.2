package info.x2a.soulshards.compat.jei;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class RecipeBackground {
    public final ResourceLocation id;
    public final int width;
    public final int height;
    public final int craftXOffset;
    public final int craftYOffset;
    public final int craftWidth;
    public final int craftHeight;
    public final Vec2[] inputPositions;
    public final Vec2[] outputPositions;

    public RecipeBackground(ResourceLocation id, int width, int height, int craftXOffset, int craftYOffset, int craftWidth, int craftHeight, Vec2[] inputPositions, Vec2[] outputPositions) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.craftXOffset = craftXOffset;
        this.craftYOffset = craftYOffset;
        this.craftWidth = craftWidth;
        this.craftHeight = craftHeight;
        this.inputPositions = inputPositions;
        this.outputPositions = outputPositions;
    }

    public IDrawable background(IGuiHelper gui) {
        return gui.createDrawable(id, 0, 0, width, height);
    }

    public void scaleForCrafting(PoseStack poses, int blocksX, int blocksY) {
        float scaleX = craftWidth / blocksX;
        float scaleY = craftHeight / blocksY;
        float scale = Math.min(scaleX, scaleY);
        poses.scale(scale, -scale, 1);
    }

    public boolean inCraftArea(int x, int y) {
        return x > craftXOffset && x < craftXOffset + craftHeight && y > craftYOffset && y < craftYOffset + craftHeight;
    }
}
