package info.x2a.soulshards.core.recipe;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.core.GsonRecipeSerializer;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

class IdByCodecAdaptor implements JsonSerializer<Item>, JsonDeserializer<Item> {

    @Override
    public Item deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Registry.ITEM.get(ResourceLocation.tryParse(jsonElement.getAsString()));
    }

    @Override
    public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(Registry.ITEM.getKey(item).toString());
    }
}

public class CursingRecipe implements Recipe<Container> {
    public static ResourceLocation ID = SoulShards.makeResource("cursing");
    @JsonAdapter(IdByCodecAdaptor.class)
    Item input;
    @JsonAdapter(IdByCodecAdaptor.class)
    Item result;
    @SerializedName("quantity")
    int resultQty;

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    public Item input() {
        return input;
    }

    @Override
    public ItemStack assemble(Container container) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        var stack = new ItemStack(result);
        stack.setCount(resultQty);
        return stack;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(null, Ingredient.of(input.getDefaultInstance()));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return new GsonRecipeSerializer<>(TypeToken.get(CursingRecipe.class));
    }

    @Override
    public RecipeType<?> getType() {
        return RegistrarSoulShards.CURSING_RECIPE.get();
    }
}
