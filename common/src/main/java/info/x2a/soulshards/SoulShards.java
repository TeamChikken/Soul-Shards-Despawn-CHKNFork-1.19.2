package info.x2a.soulshards;

import com.google.gson.reflect.TypeToken;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import info.x2a.soulshards.core.ConfigSoulShards;
import info.x2a.soulshards.core.EventHandler;
import info.x2a.soulshards.core.network.Channels;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import info.x2a.soulshards.core.data.Tier;
import info.x2a.soulshards.core.util.JsonUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class SoulShards {

    public static final String MODID = "soulshards";
    public static final Logger Log = LogManager.getLogger("Soul Shards Resewn");
    public static ConfigSoulShards CONFIG = JsonUtil.fromJson(TypeToken.get(ConfigSoulShards.class),
            new File(Platform.getConfigFolder().toFile(), MODID + "/" + MODID + ".json"),
            new ConfigSoulShards());
    public static EntityDataAccessor<Boolean> cageBornTag;
    public static GameRules.Key<GameRules.BooleanValue> allowCageSpawns;
    public static boolean IS_CLOTH_CONFIG_LOADED = Platform.isModLoaded("cloth-config");
    public static final String BOSS_TAG = "c:bosses";

    public static boolean isBoss(LivingEntity creature) {
        return creature.getTags().contains(BOSS_TAG);
    }

    public static void afterLoad() {
        Log.info("Soul Shards Despawn rises once again");
    }

    public static ResourceLocation makeResource(String name) {
        return new ResourceLocation(MODID, name);
    }

    public static void initNetwork() {
        Channels.init();
    }

    public static void initCommon() {
        Tier.readTiers();
        ConfigSoulShards.handleMultiblock();

        allowCageSpawns = GameRules.register("allowCageSpawns", GameRules.Category.SPAWNING,
                GameRules.BooleanValue.create(true));
        RegistrarSoulShards.registerBlocks();
        RegistrarSoulShards.registerItems();
        RegistrarSoulShards.registerEnchantments();
        info.x2a.soulshards.core.registry.Registries.init();
        EventHandler.init();
        initNetwork();
    }
}
