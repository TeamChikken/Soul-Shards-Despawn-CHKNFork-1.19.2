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
import info.x2a.soulshards.core.util.JsonResource;
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

    private static final JsonResource<ConfigServer> CONFIG_SERVER_RES = new JsonResource<>(new File(Platform.getConfigFolder()
                                                                                                            .toFile(), MODID + "/server.json"), new ConfigServer(), TypeToken.get(ConfigServer.class));

    private static final JsonResource<ConfigClient> CONFIG_CLIENT_RES = new JsonResource<>(
            new File(Platform.getConfigFolder().toFile(), MODID + "/client.json"),
            new ConfigClient(), TypeToken.get(ConfigClient.class));
    public static ConfigServer CONFIG_SERVER;
    public static ConfigClient CONFIG_CLIENT;
    public static EntityDataAccessor<Boolean> cageBornTag;
    public static GameRules.Key<GameRules.BooleanValue> allowCageSpawns;
    public static boolean IS_CLOTH_CONFIG_LOADED;
    public static final String BOSS_TAG = "c:bosses";

    public static boolean isBoss(LivingEntity creature) {
        return creature.getTags().contains(BOSS_TAG);
    }

    public static void afterLoad() {
        Log.info("Soul Shards Despawn rises once again");
        IS_CLOTH_CONFIG_LOADED = Platform.isModLoaded("cloth-config") || Platform.isModLoaded("cloth_config");
        CONFIG_SERVER = CONFIG_SERVER_RES.get();
        CONFIG_CLIENT = CONFIG_CLIENT_RES.get();
    }

    public static void saveClient() {
        CONFIG_CLIENT_RES.save();
    }

    public static void saveServer() {
        CONFIG_SERVER_RES.save();
    }

    public static ResourceLocation makeResource(String name) {
        return new ResourceLocation(MODID, name);
    }

    public static void initNetwork() {
        Channels.init();
    }

    public static void initCommon() {
        afterLoad();
        Tier.readTiers();
        ConfigServer.handleMultiblock();
        PlayerEvent.PLAYER_JOIN.register(p -> {
            if (!p.isLocalPlayer() && !p.getServer().isSingleplayer()) {
                Channels.CONFIG_UPDATE.sendToPlayer(p, new ConfigUpdate(CONFIG_SERVER));
            }
        });

        allowCageSpawns = GameRules.register("allowCageSpawns", GameRules.Category.SPAWNING,
                GameRules.BooleanValue.create(true));
        RegistrarSoulShards.registerRecipes();
        RegistrarSoulShards.registerBlocks();
        RegistrarSoulShards.registerItems();
        RegistrarSoulShards.registerEnchantments();
        info.x2a.soulshards.core.registry.Registries.init();
        EventHandler.init();
        initNetwork();
    }
}
