package info.x2a.soulshards.core;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import info.x2a.soulshards.core.util.JsonUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class GsonRecipeSerializer<R extends Recipe<?>> implements RecipeSerializer<R> {
    private final TypeToken<R> token;

    public GsonRecipeSerializer(TypeToken<R> token) {
        this.token = token;
    }

    @Override
    public @NotNull R fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
        return JsonUtil.fromJson(token, jsonObject);
    }

    @Override
    public @NotNull R fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        return JsonUtil.fromJson(token, friendlyByteBuf.readUtf());
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, R recipe) {
        friendlyByteBuf.writeUtf(JsonUtil.getJson(recipe, token));
    }
}
