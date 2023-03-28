package info.n4tomic.soulshards.core;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import info.n4tomic.soulshards.SoulShards;
import info.n4tomic.soulshards.block.BlockSoulCage;
import info.n4tomic.soulshards.block.TileEntitySoulCage;
import info.n4tomic.soulshards.core.util.EnchantmentSoulStealer;
import info.n4tomic.soulshards.item.ItemSoulShard;
import info.n4tomic.soulshards.item.ItemVileSword;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;

public class RegistrarSoulShards {

    public static final Block SOUL_CAGE = new BlockSoulCage();

    public static final BlockEntityType<TileEntitySoulCage> SOUL_CAGE_TE =
            BlockEntityType.Builder.of(TileEntitySoulCage::new, SOUL_CAGE).build(null);

    public static final Item SOUL_SHARD = new ItemSoulShard();
    public static final Item VILE_SWORD = new ItemVileSword();
    public static final Item CORRUPTED_ESSENCE = new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Item CORRUPTED_INGOT = new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Item VILE_DUST = new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC));

    public static final Enchantment SOUL_STEALER = new EnchantmentSoulStealer();

    public static void registerBlocks(Registries regs) {
        regs.get(Registry.BLOCK_REGISTRY).register(new ResourceLocation(SoulShards.MODID, "soul_cage"), () -> SOUL_CAGE);
        regs.get(Registry.BLOCK_ENTITY_TYPE_REGISTRY).register(new ResourceLocation(SoulShards.MODID, "soul_cage"),
                () -> SOUL_CAGE_TE);
    }

    public static void registerItems(Registrar<Item> registry) {
        registry.register(new ResourceLocation(SoulShards.MODID, "soul_cage"), () -> new BlockItem(SOUL_CAGE,
                new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
        registry.register(new ResourceLocation(SoulShards.MODID, "soul_shard"), () -> SOUL_SHARD);
        registry.register(new ResourceLocation(SoulShards.MODID, "vile_sword"), () -> VILE_SWORD);
        registry.register(new ResourceLocation(SoulShards.MODID, "corrupted_essence"), () -> CORRUPTED_ESSENCE);
        registry.register(new ResourceLocation(SoulShards.MODID, "corrupted_ingot"), () -> CORRUPTED_INGOT);
        registry.register(new ResourceLocation(SoulShards.MODID, "vile_dust"), () -> VILE_DUST);
    }

    public static void registerEnchantments(Registry<Enchantment> registry) {
        Registry.register(registry, new ResourceLocation(SoulShards.MODID, "soul_stealer"), SOUL_STEALER);
    }
}
