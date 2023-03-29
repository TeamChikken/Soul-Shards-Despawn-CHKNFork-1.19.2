package info.x2a.soulshards.core.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.api.IShardTier;
import info.x2a.soulshards.block.BlockSoulCage;
import info.x2a.soulshards.block.TileEntitySoulCage;
import info.x2a.soulshards.core.data.Binding;
import info.x2a.soulshards.core.data.Tier;
import info.x2a.soulshards.core.util.EnchantmentSoulStealer;
import info.x2a.soulshards.item.ItemSoulShard;
import info.x2a.soulshards.item.ItemVileSword;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RegistrarSoulShards {

    public static RegistrySupplier<BlockSoulCage> SOUL_CAGE;

    public static RegistrySupplier<BlockEntityType<TileEntitySoulCage>> SOUL_CAGE_TE;

    public static RegistrySupplier<ItemSoulShard> SOUL_SHARD;
    //public static final RegistrySupplier<Item> VILE_SWORD; = new ItemVileSword();

    public static RegistrySupplier<Item> CORRUPTED_INGOT;
    public static RegistrySupplier<Enchantment> SOUL_STEALER;
    public static List<ItemStack> CREATIVE_TAB_ITEMS = new ArrayList<>();

    public static void init() {
        registerBlocks();
        registerItems();
        registerEnchantments();
        CreativeTabRegistry.create(new ResourceLocation(SoulShards.MODID
                        , "creative_tab"),
                (CreativeModeTab.Builder builder) -> builder.icon(() -> new ItemSoulShard().getDefaultInstance()).displayItems((params, output) -> {
                    output.accept(new ItemSoulShard());
                    for (IShardTier tier : Tier.INDEXED) {
                        var item = new ItemSoulShard();
                        ItemStack stack = new ItemStack(item);
                        Binding binding = new Binding(null, tier.getKillRequirement());
                        item.updateBinding(stack, binding);
                        output.accept(item);
                    }
                    output.acceptAll(CREATIVE_TAB_ITEMS);
                }));
    }

    public static <T extends Item> RegistrySupplier<T> registerAndAddCreative(DeferredRegister<Item> reg,
                                                                              ResourceLocation id,
                                                                              Supplier<T> prov) {
        CREATIVE_TAB_ITEMS.add(new ItemStack(prov.get()));
        return reg.register(id, prov);
    }

    public static void registerBlocks() {
        SOUL_CAGE = SoulRegistries.BLOCKS.register(new ResourceLocation(SoulShards.MODID, "soul_cage"), BlockSoulCage::new);
        SOUL_CAGE_TE = SoulRegistries.BLOCK_ENTITIES.register(new ResourceLocation(SoulShards.MODID, "soul_cage"),
                () -> BlockEntityType.Builder.of(TileEntitySoulCage::new, SOUL_CAGE.get()).build(null));
        SoulRegistries.BLOCKS.register();
        SoulRegistries.BLOCK_ENTITIES.register();
    }

    private static <T extends Item> RegistrySupplier<T> regItem(String id, Supplier<T> source) {
        return registerAndAddCreative(SoulRegistries.ITEMS, new ResourceLocation(SoulShards.MODID, id), source);
    }

    public static void registerItems() {
        var registry = SoulRegistries.ITEMS;
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
