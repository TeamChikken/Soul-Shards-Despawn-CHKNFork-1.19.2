package info.x2a.soulshards;

import com.google.gson.reflect.TypeToken;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import info.x2a.soulshards.core.config.ConfigClient;
import info.x2a.soulshards.core.config.ConfigServer;
import info.x2a.soulshards.core.EventHandler;
import info.x2a.soulshards.core.network.Channels;
import info.x2a.soulshards.core.network.message.ConfigUpdate;
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
    public static final Logger Log = LogManager.getLogger("Soul Shards Despawn");
    public static ConfigServer CONFIG_SERVER = JsonUtil.fromJson(TypeToken.get(ConfigServer.class),
            new File(Platform.getConfigFolder().toFile(), MODID + "/server.json"),
            new ConfigServer());

    public static ConfigClient CONFIG_CLIENT = JsonUtil.fromJson(TypeToken.get(ConfigClient.class),
            new File(Platform.getConfigFolder().toFile(), MODID + "/client.json"),
            new ConfigClient());
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
        ConfigServer.handleMultiblock();
        PlayerEvent.PLAYER_JOIN.register(p -> {
            Channels.CONFIG_UPDATE.sendToPlayer(p, new ConfigUpdate(CONFIG_SERVER));
        });

        allowCageSpawns = GameRules.register("allowCageSpawns", GameRules.Category.SPAWNING,
                GameRules.BooleanValue.create(true));
        RegistrarSoulShards.registerBlocks();
        RegistrarSoulShards.registerItems();
        RegistrarSoulShards.registerEnchantments();
        info.x2a.soulshards.core.registry.Registries.init();
        EventHandler.init();
        initNetwork();
        afterLoad();
    }
}
