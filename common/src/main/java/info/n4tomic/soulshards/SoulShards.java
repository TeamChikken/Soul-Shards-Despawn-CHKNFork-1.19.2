package info.n4tomic.soulshards;

import com.google.common.base.Suppliers;
import com.google.gson.reflect.TypeToken;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registries;
import info.n4tomic.soulshards.core.ConfigSoulShards;
import info.n4tomic.soulshards.core.EventHandler;
import info.n4tomic.soulshards.core.RegistrarSoulShards;
import info.n4tomic.soulshards.core.data.Tier;
import info.n4tomic.soulshards.core.util.JsonUtil;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.function.Supplier;

public class SoulShards {

    public static final String MODID = "soulshardsresewn";
    public static final Logger Log = LogManager.getLogger("Soul Shards Resewn");
    public static ConfigSoulShards CONFIG = JsonUtil.fromJson(TypeToken.get(ConfigSoulShards.class),
            new File(Platform.getConfigFolder().toFile(), MODID + "/" + MODID + ".json"),
            new ConfigSoulShards());
    public static EntityDataAccessor<Boolean> cageBornTag;
    public static GameRules.Key<GameRules.BooleanValue> allowCageSpawns;
    public static final String BOSS_TAG = "c:bosses";

    public static boolean isBoss(LivingEntity creature) {
        return creature.getTags().contains(BOSS_TAG);
    }

    public static void afterLoad() {
        Log.info("Soul Shards Resewn rises once again");
    }

    private static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MODID));

    public static void initCommon() {
        Tier.readTiers();
        ConfigSoulShards.handleMultiblock();

        allowCageSpawns = GameRules.register("allowCageSpawns", GameRules.Category.SPAWNING,
                GameRules.BooleanValue.create(true));
        RegistrarSoulShards.registerBlocks(REGISTRIES.get());
        RegistrarSoulShards.registerItems(REGISTRIES.get().get(Registry.ITEM_REGISTRY));
        RegistrarSoulShards.registerEnchantments(Registry.ENCHANTMENT);
        EventHandler.init();
    }
}
