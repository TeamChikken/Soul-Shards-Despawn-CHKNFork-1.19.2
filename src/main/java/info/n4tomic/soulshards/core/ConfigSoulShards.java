package info.n4tomic.soulshards.core;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import info.n4tomic.soulshards.core.data.MultiblockPattern;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;

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
        // FIXME parsing is currently broke
//        File multiblockFile = new File(FabricLoader.INSTANCE.getConfigDirectory(), SoulShards.MODID + "/multiblock.json");
//        if (!multiblockFile.exists()) {
//            try {
//                FileUtils.copyInputStreamToFile(ConfigSoulShards.class.getResourceAsStream("/data/multiblock.json"), multiblockFile);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        multiblock = JsonUtil.fromJson(TypeToken.get(MultiblockPattern.class), multiblockFile);
        if (multiblock == null)
            multiblock = MultiblockPattern.DEFAULT;
    }

    public static MultiblockPattern getMultiblock() {
        if (multiblock == null)
            handleMultiblock();

        return multiblock;
    }

    public static class ConfigBalance {

        private boolean allowSpawnerAbsorption;
        private int absorptionBonus;
        private boolean allowBossSpawns;
        private boolean countCageBornForShard;
        private boolean requireOwnerOnline;
        private boolean requireRedstoneSignal;
        private boolean allowShardCombination;
        private int spawnCap;

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

        public ConfigBalance() {
            this(true, 200, false, false, false, false, true, 32);
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
        private static final Set<String> DEFAULT_DISABLES = Sets.newHashSet(
                "minecraft:armor_stand",
                "minecraft:elder_guardian",
                "minecraft:ender_dragon",
                "minecraft:wither",
                "minecraft:wither",
                "minecraft:player"
        );

        private Map<ResourceLocation, Boolean> entities;

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
