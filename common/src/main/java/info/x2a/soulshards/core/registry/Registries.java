package info.x2a.soulshards.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import info.x2a.soulshards.SoulShards;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class Registries {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(SoulShards.MODID,
            Registry.ITEM_REGISTRY);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(SoulShards.MODID,
            Registry.BLOCK_REGISTRY);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(SoulShards.MODID,
            Registry.ENCHANTMENT_REGISTRY);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(SoulShards.MODID,
            Registry.BLOCK_ENTITY_TYPE_REGISTRY);

    public static void init() {
    }
}
