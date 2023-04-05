package info.x2a.soulshards.core.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.api.IShardTier;
import info.x2a.soulshards.block.BlockSoulCage;
import info.x2a.soulshards.block.BlockCursedFire;
import info.x2a.soulshards.block.TileEntitySoulCage;
import info.x2a.soulshards.core.data.Binding;
import info.x2a.soulshards.core.data.Tier;
import info.x2a.soulshards.core.util.EnchantmentSoulStealer;
import info.x2a.soulshards.item.ItemQuartzAndSteel;
import info.x2a.soulshards.item.ItemSoulShard;
import info.x2a.soulshards.item.ItemVileSword;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    public static List<RegistrySupplier<? extends Item>> CREATIVE_TAB_ITEMS = new ArrayList<>();

    public static void init() {
        registerBlocks();
        registerItems();
        registerEnchantments();
        CreativeTabRegistry.create(new ResourceLocation(SoulShards.MODID
                        , "creative_tab"),
                (CreativeModeTab.Builder builder) -> builder.icon(() -> new ItemStack(SOUL_SHARD.get())).displayItems((params, output) -> {
                    var shard = SOUL_SHARD.get();
                    for (IShardTier tier : Tier.INDEXED) {
                        ItemStack stack = new ItemStack(shard);
                        Binding binding = new Binding(null, tier.getKillRequirement());
                        shard.updateBinding(stack, binding);
                        output.accept(stack);
                    }
                    output.acceptAll(CREATIVE_TAB_ITEMS.stream().map(s -> new ItemStack(s.get())).collect(Collectors.toList()));
                }));
    }

    public static <T extends Item> RegistrySupplier<T> registerAndAddCreative(DeferredRegister<Item> reg,
                                                                              ResourceLocation id,
                                                                              Supplier<T> prov) {
        var supplier = reg.register(id, prov);
        CREATIVE_TAB_ITEMS.add(supplier);
        return supplier;
    }

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
        SOUL_CAGE = SoulRegistries.BLOCKS.register(new ResourceLocation(SoulShards.MODID, "soul_cage"), BlockSoulCage::new);
        SOUL_CAGE_TE = SoulRegistries.BLOCK_ENTITIES.register(new ResourceLocation(SoulShards.MODID, "soul_cage"),
                () -> BlockEntityType.Builder.of(TileEntitySoulCage::new, SOUL_CAGE.get()).build(null));
        SoulRegistries.BLOCKS.register();
        SoulRegistries.BLOCK_ENTITIES.register();
    }

    private static <T extends Item> RegistrySupplier<T> regItem(String id, Supplier<T> source) {
        return registerAndAddCreative(SoulRegistries.ITEMS, new ResourceLocation(SoulShards.MODID, id), source);
    }

    public static void registerRecipes() {
        CURSING_RECIPE = Registries.RECIPES.register(CursingRecipe.ID, () -> new RecipeType<>() {
            @Override
            public String toString() {
                return "cursing";
            }
        });
        Registries.RECIPE_SERIALIZERS.register(CursingRecipe.ID, () -> new GsonRecipeSerializer<>(TypeToken.get(CursingRecipe.class)));
        Registries.RECIPES.register();
        Registries.RECIPE_SERIALIZERS.register();
        SoulShards.Log.info("Recipes registered");
    }

    public static void registerItems() {
        regItem("soul_cage", () -> new BlockItem(SOUL_CAGE.get(), new Item.Properties()));
        regItem("vile_dust", () -> new Item(new Item.Properties()));
        regItem("vile_sword", ItemVileSword::new);
        regItem("corrupted_essence", () -> new Item(new Item.Properties()));
        SOUL_SHARD = regItem("soul_shard", ItemSoulShard::new);
        CORRUPTED_INGOT = regItem("corrupted_ingot",
                () -> new Item(new Item.Properties()));
        SoulRegistries.ITEMS.register();
    }

    public static void registerEnchantments() {
        SOUL_STEALER = SoulRegistries.ENCHANTMENTS.register(new ResourceLocation(SoulShards.MODID, "soul_stealer"),
                EnchantmentSoulStealer::new);
        SoulRegistries.ENCHANTMENTS.register();
    }
}
