package info.x2a.soulshards.core.recipe;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.core.util.GsonRecipeSerializer;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import info.x2a.soulshards.core.util.JsonUtil;
import info.x2a.soulshards.core.util.RecipeSerde;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

class IdByCodecAdaptor implements JsonSerializer<Item>, JsonDeserializer<Item> {

    @Override
    public Item deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(jsonElement.getAsString()));
    }

    @Override
    public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(BuiltInRegistries.ITEM.getKey(item).toString());
    }
}

public class CursingRecipe implements RecipeSerde<Container> {
    public static ResourceLocation ID = SoulShards.makeResource("cursing");

    @JsonUtil.JsonSkip
    private ResourceLocation id;
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
    public ItemStack assemble(Container container, RegistryAccess access) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return getResult();
    }

    public ItemStack getResult() {
        var stack = new ItemStack(result);
        stack.setCount(resultQty);
        return stack;
    }

    @Override
    public ResourceLocation getId() {
        return id;
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

    @Override
    public void setId(ResourceLocation id) {
        this.id = id;
    }
}
