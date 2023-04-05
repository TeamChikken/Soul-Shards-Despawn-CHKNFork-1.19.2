package info.x2a.soulshards.core.registry;

import com.google.gson.reflect.TypeToken;
import dev.architectury.registry.registries.RegistrySupplier;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.block.BlockHallowedFire;
import info.x2a.soulshards.block.BlockSoulCage;
import info.x2a.soulshards.block.BlockCursedFire;
import info.x2a.soulshards.block.TileEntitySoulCage;
import info.x2a.soulshards.core.GsonRecipeSerializer;
import info.x2a.soulshards.core.recipe.CursingRecipe;
import info.x2a.soulshards.core.util.EnchantmentSoulStealer;
import info.x2a.soulshards.item.ItemQuartzAndSteel;
import info.x2a.soulshards.item.ItemSoulShard;
import info.x2a.soulshards.item.ItemVileSword;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class RegistrarSoulShards {

    public static RegistrySupplier<BlockSoulCage> SOUL_CAGE;
    public static RegistrySupplier<BlockCursedFire> CURSED_FIRE;
    public static RegistrySupplier<BlockHallowedFire> HALLOWED_FIRE;

    public static RegistrySupplier<BlockEntityType<TileEntitySoulCage>> SOUL_CAGE_TE;

    public static RegistrySupplier<ItemSoulShard> SOUL_SHARD;
    public static RegistrySupplier<ItemQuartzAndSteel> QUARTZ_AND_STEEL;
    //public static final RegistrySupplier<Item> VILE_SWORD; = new ItemVileSword();

    public static RegistrySupplier<Item> CORRUPTED_INGOT;
    public static RegistrySupplier<Item> CORRUPTED_ESSENCE;
    public static RegistrySupplier<Enchantment> SOUL_STEALER;

    public static RegistrySupplier<RecipeType<CursingRecipe>> CURSING_RECIPE;

    public static void registerBlocks() {
        CURSED_FIRE = Registries.BLOCKS.register(SoulShards.makeResource("cursed_fire"), BlockCursedFire::new);
        HALLOWED_FIRE = Registries.BLOCKS.register(SoulShards.makeResource("hallowed_fire"), BlockHallowedFire::new);
        SOUL_CAGE = Registries.BLOCKS.register(new ResourceLocation(SoulShards.MODID, "soul_cage"), BlockSoulCage::new);
        SOUL_CAGE_TE = Registries.BLOCK_ENTITIES.register(new ResourceLocation(SoulShards.MODID, "soul_cage"),
                () -> BlockEntityType.Builder.of(TileEntitySoulCage::new, SOUL_CAGE.get())
                        .build(null));
        Registries.BLOCKS.register();
        Registries.BLOCK_ENTITIES.register();
    }

    public static void registerRecipes() {
        CURSING_RECIPE = Registries.RECIPES.register(SoulShards.makeResource("cursing"), () -> new RecipeType<CursingRecipe>() {
            @Override
            public String toString() {
                return "cursing";
            }
        });
        Registries.RECIPE_SERIALIZERS.register(SoulShards.makeResource("cursing"), () -> new GsonRecipeSerializer<CursingRecipe>(TypeToken.get(CursingRecipe.class)));
        Registries.RECIPES.register();
        Registries.RECIPE_SERIALIZERS.register();
        SoulShards.Log.info("Recipes registered");
    }

    public static void registerItems() {
        var registry = Registries.ITEMS;
        registry.register(new ResourceLocation(SoulShards.MODID, "soul_cage"),
                () -> new BlockItem(SOUL_CAGE.get(),
                        new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
        QUARTZ_AND_STEEL = registry.register(SoulShards.makeResource("quartz_and_steel"), ItemQuartzAndSteel::new);
        SOUL_SHARD = registry.register(new ResourceLocation(SoulShards.MODID, "soul_shard"),
                ItemSoulShard::new);
        registry.register(new ResourceLocation(SoulShards.MODID, "vile_sword"), ItemVileSword::new);
        CORRUPTED_ESSENCE = registry.register(new ResourceLocation(SoulShards.MODID, "corrupted_essence"), () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
        CORRUPTED_INGOT = registry.register(new ResourceLocation(SoulShards.MODID, "corrupted_ingot"),
                () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
        registry.register(new ResourceLocation(SoulShards.MODID, "vile_dust"), () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
        Registries.ITEMS.register();
    }

    public static void registerEnchantments() {
        SOUL_STEALER = Registries.ENCHANTMENTS.register(new ResourceLocation(SoulShards.MODID, "soul_stealer"),
                EnchantmentSoulStealer::new);
        Registries.ENCHANTMENTS.register();
    }
}
