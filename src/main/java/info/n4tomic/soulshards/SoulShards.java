package info.n4tomic.soulshards;

import com.google.gson.reflect.TypeToken;
import info.n4tomic.soulshards.core.ConfigSoulShards;
import info.n4tomic.soulshards.core.EventHandler;
import info.n4tomic.soulshards.core.RegistrarSoulShards;
import info.n4tomic.soulshards.core.data.Tier;
import info.n4tomic.soulshards.core.util.JsonUtil;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import java.io.File;

public class SoulShards implements ModInitializer {

    public static final String MODID = "soulshards";
    public static ConfigSoulShards CONFIG = JsonUtil.fromJson(TypeToken.get(ConfigSoulShards.class),
            new File(QuiltLoader.getConfigDir().toFile(), MODID + "/" + MODID + ".json"),
            new ConfigSoulShards());
    public static EntityDataAccessor<Boolean> cageBornTag;
    public static GameRules.Key<GameRules.BooleanValue> allowCageSpawns;
    public static final String BOSS_TAG = "c:bosses";

    public static boolean isBoss(LivingEntity creature) {
        return creature.getTags().contains(BOSS_TAG);
    }

    @Override
    public void onInitialize(ModContainer container) {
        Tier.readTiers();
        ConfigSoulShards.handleMultiblock();
        allowCageSpawns = GameRuleRegistry.register("allowCageSpawns", GameRules.Category.SPAWNING,
                GameRuleFactory.createBooleanRule(true));
        RegistrarSoulShards.registerBlocks(Registry.BLOCK);
        RegistrarSoulShards.registerItems(Registry.ITEM);
        RegistrarSoulShards.registerEnchantments(Registry.ENCHANTMENT);
        EventHandler.init();
    }
}
