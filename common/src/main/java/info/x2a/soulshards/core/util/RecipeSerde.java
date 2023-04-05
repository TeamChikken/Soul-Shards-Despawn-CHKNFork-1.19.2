package info.x2a.soulshards.core.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public interface RecipeSerde<T extends Container> extends Recipe<T> {
    void setId(ResourceLocation id);
}
