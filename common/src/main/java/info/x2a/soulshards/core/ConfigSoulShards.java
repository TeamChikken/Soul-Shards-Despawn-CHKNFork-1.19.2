package info.x2a.soulshards.core;

import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import dev.architectury.platform.Platform;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.core.data.MultiblockPattern;
import info.x2a.soulshards.core.util.JsonUtil;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigSoulShards {

    private static MultiblockPattern multiblock;

    private ConfigBalance balance;
    private ConfigClient client;
    private ConfigEntityList entityList;

    private ConfigSoulShards(ConfigBalance balance, ConfigClient client, ConfigEntityList entityList) {
        this.balance = balance;
        this.client = client;
        this.entityList = entityList;
    }

    public ConfigSoulShards() {
        this(new ConfigBalance(), new ConfigClient(), new ConfigEntityList());
    }

    public ConfigBalance getBalance() {
        return balance;
    }

    public ConfigClient getClient() {
        return client;
    }

    public ConfigEntityList getEntityList() {
        return entityList;
    }

    public static void handleMultiblock() {
        File multiblockFile = new File(Platform.getConfigFolder().toFile(), SoulShards.MODID + "/multiblock.json");
        if (!multiblockFile.exists()) {
            try {
                FileUtils.copyInputStreamToFile(ConfigSoulShards.class.getResourceAsStream("/data/multiblock.json"),
                        multiblockFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        multiblock = JsonUtil.fromJson(TypeToken.get(MultiblockPattern.class), multiblockFile);
        if (multiblock == null) {
            multiblock = MultiblockPattern.DEFAULT;
        }
    }

    public static MultiblockPattern getMultiblock() {
        if (multiblock == null)
            handleMultiblock();

        return multiblock;
    }

    public static class ConfigBalance {

        public boolean allowSpawnerAbsorption;
        public int absorptionBonus;
        public boolean allowBossSpawns;
        public boolean countCageBornForShard;
        public boolean requireOwnerOnline;
        public boolean requireRedstoneSignal;
        public boolean allowShardCombination;
        public int spawnCap;

        public ConfigBalance(boolean allowSpawnerAbsorption, int absorptionBonus, boolean allowBossSpawns, boolean countCageBornForShard, boolean requireOwnerOnline, boolean requireRedstoneSignal, boolean allowShardCombination, int spawnCap) {
            this.allowSpawnerAbsorption = allowSpawnerAbsorption;
            this.absorptionBonus = absorptionBonus;
            this.allowBossSpawns = allowBossSpawns;
            this.countCageBornForShard = countCageBornForShard;
            this.requireOwnerOnline = requireOwnerOnline;
            this.requireRedstoneSignal = requireRedstoneSignal;
            this.allowShardCombination = allowShardCombination;
            this.spawnCap = spawnCap;
        }

        private static final ConfigBalance DEFAULT = new ConfigBalance();

        public ConfigBalance() {
            this(true, 200, false, false, false, false, true, 32);
        }

        public void addToBuilder(ConfigBuilder builder) {
        }


        public boolean allowSpawnerAbsorption() {
            return allowSpawnerAbsorption;
        }

        public int getAbsorptionBonus() {
            return absorptionBonus;
        }

        public boolean allowBossSpawns() {
            return allowBossSpawns;
        }

        public boolean countCageBornForShard() {
            return countCageBornForShard;
        }

        public boolean requireOwnerOnline() {
            return requireOwnerOnline;
        }

        public boolean requireRedstoneSignal() {
            return requireRedstoneSignal;
        }

        public boolean allowShardCombination() {
            return allowShardCombination;
        }

        public int getSpawnCap() {
            return spawnCap;
        }
    }

    public static class ConfigClient {
        private boolean displayDurabilityBar;

        public ConfigClient(boolean displayDurabilityBar) {
            this.displayDurabilityBar = displayDurabilityBar;
        }

        public ConfigClient() {
            this(true);
        }

        public boolean displayDurabilityBar() {
            return displayDurabilityBar;
        }
    }

    public static class ConfigEntityList {
        private static final Set<ResourceLocation> DEFAULT_DISABLES = createDefaultDisables();

        private static Set<ResourceLocation> createDefaultDisables() {
            String[] disablesVanilla = {
                    "armor_stand",
                    "elder_guardian",
                    "ender_dragon",
                    "wither",
                    "wither",
                    "player"};
            var output = new HashSet<ResourceLocation>();
            for (var el :
                    disablesVanilla) {
                output.add(new ResourceLocation("minecraft", el));
            }
            return output;
        }

        private final Map<ResourceLocation, Boolean> entities;

        public ConfigEntityList(Map<ResourceLocation, Boolean> entities) {
            this.entities = entities;
        }

        public ConfigEntityList() {
            this(getDefaults());
        }

        public boolean isEnabled(ResourceLocation entityId) {
            return entities.getOrDefault(entityId, true);
        }

        private static Map<ResourceLocation, Boolean> getDefaults() {
            Map<ResourceLocation, Boolean> defaults = Maps.newHashMap();
            Registry.ENTITY_TYPE.stream().filter(e -> e.getCategory() == MobCategory.MISC)
                                .forEach(e -> {
                                    var entityId = Registry.ENTITY_TYPE.getKey(e);
                                    if (DEFAULT_DISABLES.contains(entityId)) {
                                        defaults.put(entityId, false);
                                    }
                                });
            return defaults;
        }
    }
}
