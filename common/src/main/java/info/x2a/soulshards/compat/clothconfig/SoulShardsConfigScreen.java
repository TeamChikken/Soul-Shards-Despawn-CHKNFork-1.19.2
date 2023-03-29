package info.x2a.soulshards.compat.clothconfig;


import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.core.ConfigSoulShards;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SoulShardsConfigScreen {
    private final Screen popup;
    private static final ConfigSoulShards DEFAULT_CONFIG = new ConfigSoulShards();

    public SoulShardsConfigScreen(Screen parent) {
        var builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.translatable("title" +
                ".soulshards" +
                ".config")).setSavingRunnable(this::onSave);
        var entry = builder.entryBuilder();
        var cfg = SoulShards.CONFIG;
        var balance = cfg.getBalance();
        builder.getOrCreateCategory(Component.translatable("category.soulshards.balance"))
               .addEntry(entry.startBooleanToggle(Component.translatable("option.soulshards.allow_shard_combine"), balance.allowShardCombination)
                              .setTooltip(Component.translatable("tooltip.soulshards.allow_shard_combine"))
                              .setDefaultValue(DEFAULT_CONFIG.getBalance().allowShardCombination)
                              .setSaveConsumer(v -> balance.allowShardCombination = v)
                       .build())
               .addEntry(entry.startBooleanToggle(Component.translatable("option.soulshards.allow_boss_spawns"), balance.allowBossSpawns)
                              .setDefaultValue(DEFAULT_CONFIG.getBalance().allowBossSpawns)
                              .setSaveConsumer(v -> balance.allowBossSpawns = v)
                              .setTooltip(Component.translatable("tooltip.soulshards.allow_boss_spawns"))
                       .build())
               .addEntry(entry.startIntField(Component.translatable("option.soulshards.absorb_bonus"), balance.absorptionBonus)
                              .setDefaultValue(DEFAULT_CONFIG.getBalance().absorptionBonus)
                              .setSaveConsumer(v -> balance.absorptionBonus = v)
                              .setTooltip(Component.translatable("tooltip.soulshards.absorb_bonus"))
                       .build())
               .addEntry(entry.startBooleanToggle(Component.translatable("option.soulshards.count_cage_born"), balance.countCageBornForShard)
                              .setTooltip(Component.translatable("tooltip.soulshards.count_cage_born"))
                              .setSaveConsumer(v -> balance.countCageBornForShard = v)
                              .setDefaultValue(DEFAULT_CONFIG.getBalance().countCageBornForShard)
                       .build())
               .addEntry(entry.startBooleanToggle(Component.translatable("option.soulshards.require_owner_online"), balance.requireOwnerOnline)
                              .setSaveConsumer(v -> balance.requireOwnerOnline = v)
                              .setTooltip(Component.translatable("tooltip.soulshards.require_owner_online"))
                              .setDefaultValue(DEFAULT_CONFIG.getBalance().requireOwnerOnline)
                       .build())
               .addEntry(entry.startBooleanToggle(Component.translatable("option.soulshards.require_redstone"), balance.requireRedstoneSignal)
                              .setDefaultValue(DEFAULT_CONFIG.getBalance().requireRedstoneSignal)
                              .setSaveConsumer(v -> balance.requireRedstoneSignal = v)
                              .setTooltip(Component.translatable("tooltip.soulshards.require_redstone"))
                       .build())
               .addEntry(entry.startIntField(Component.translatable("option.soulshards.spawn_cap"), balance.spawnCap)
                              .setDefaultValue(DEFAULT_CONFIG.getBalance().spawnCap)
                              .setSaveConsumer(v -> balance.spawnCap = v)
                              .setTooltip(Component.translatable("tooltip.soulshards.spawn_cap"))
                       .build())
        ;
        //SoulShards.CONFIG.getBalance().addToBuilder(builder);
        popup = builder.build();
    }

    public Screen screen() {
        return popup;
    }

    private void onSave() {

    }
}
