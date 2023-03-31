package info.x2a.soulshards.block;

import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.api.CageSpawnEvent;
import info.x2a.soulshards.api.IShardTier;
import info.x2a.soulshards.api.ISoulShard;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import info.x2a.soulshards.core.data.Binding;
import info.x2a.soulshards.item.ItemSoulShard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TileEntitySoulCage extends BlockEntity {

    private final Container inventory;
    private long activeTime;
    private boolean active;

    public TileEntitySoulCage(BlockPos pos, BlockState state) {
        super(RegistrarSoulShards.SOUL_CAGE_TE.get(), pos, state);

        this.inventory = new SimpleContainer(1) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                if (!(stack.getItem() instanceof ItemSoulShard))
                    return false;

                Binding binding = ((ItemSoulShard) stack.getItem()).getBinding(stack);
                return binding != null && binding.getBoundEntity() != null && SoulShards.CONFIG_SERVER.getEntityList()
                                                                                                      .isEnabled(binding.getBoundEntity());
            }
        };
    }

    private Optional<Binding> canSpawn(Level level, BlockPos pos) {

        if (!level.getGameRules().getBoolean(SoulShards.allowCageSpawns)) {
            return Optional.empty();
        }
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() != RegistrarSoulShards.SOUL_CAGE.get())
            return Optional.empty();

        ItemStack shardStack = inventory.getItem(0);
        if (shardStack.isEmpty() || !(shardStack.getItem() instanceof ItemSoulShard))
            return Optional.empty();

        Binding binding = ((ItemSoulShard) shardStack.getItem()).getBinding(shardStack);
        if (binding == null || binding.getBoundEntity() == null)
            return Optional.empty();

        IShardTier tier = binding.getTier();
        if (tier.getSpawnAmount() == 0)
            return Optional.empty();

        if (SoulShards.CONFIG_SERVER.getBalance().requireOwnerOnline() && !ownerOnline())
            return Optional.empty();

        if (!SoulShards.CONFIG_SERVER.getEntityList().isEnabled(binding.getBoundEntity()))
            return Optional.empty();

        if (!SoulShards.CONFIG_SERVER.getBalance().requireRedstoneSignal()) {
            if (state.getValue(BlockSoulCage.POWERED) && tier.checkRedstone())
                return Optional.empty();
        } else if (!state.getValue(BlockSoulCage.POWERED))
            return Optional.empty();

        if (tier.checkPlayer() && level.getNearestPlayer(pos.getX(), pos.getY(), pos.getX(), 16,
                false) == null)
            return Optional.empty();

        return Optional.of(binding);
    }

    public static void ticker(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof TileEntitySoulCage me) {
            me.tick();
        }
    }

    public void setShard(ItemStack shard) {
        inventory.setItem(0, shard);
        activeTime = ((ISoulShard) shard.getItem()).getBinding(shard).getTier().getCooldown();
    }

    public void tick() {
        if (level == null || level.isClientSide)
            return;

        var pos = getBlockPos();
        var result = canSpawn(level, pos);
        if (result.isEmpty()) {
            if (active) {
                setState(false);
                activeTime = 0;
                level.updateNeighborsAt(pos, getBlockState().getBlock());
            }
            return;
        }

        if (!active) {
            setState(true);
            activeTime = activeTime % result.get().getTier().getCooldown();
            level.updateNeighborsAt(pos, getBlockState().getBlock());
        }
        activeTime++;

        if (activeTime % result.get().getTier().getCooldown() == 0)
            spawnEntities();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        if (tag.contains("shard"))
            inventory.setItem(0, ItemStack.of(tag.getCompound("shard")));
        this.active = tag.getBoolean("active");
        this.activeTime = tag.getLong("activeTime");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        ItemStack shardStack = inventory.getItem(0);
        if (!shardStack.isEmpty())
            tag.put("shard", shardStack.save(new CompoundTag()));
        tag.putBoolean("active", active);
        tag.putLong("activeTime", activeTime);
        super.saveAdditional(tag);
    }

    private void spawnEntities() {
        Binding binding = getBinding();
        if (binding == null || binding.getBoundEntity() == null)
            return;

        var toSpawn = Registry.ENTITY_TYPE.get(binding.getBoundEntity());
        IShardTier tier = binding.getTier();
        var pos = getBlockPos();
        var level = getLevel();
        assert level != null;

        spawnLoop:
        for (int i = 0; i < tier.getSpawnAmount(); i++) {
            for (int attempts = 0; attempts < 5; attempts++) {
                double x = pos.getX() + (level.random.nextDouble() - level.random.nextDouble()) * 4.0D;
                double y = pos.getY() + level.random.nextInt(3) - 1;
                double z = pos.getZ() + (level.random.nextDouble() - level.random.nextDouble()) * 4.0D;
                BlockPos spawnAt = new BlockPos(x, y, z);

                var spawned = (LivingEntity) toSpawn.create(level);
                if (spawned == null)
                    continue;

                if (tier.checkLight() && !canSpawnInLight(spawned, spawnAt))
                    continue;

                spawned.moveTo(spawnAt, level.random.nextFloat() * 360F, 0F);
                spawned.getEntityData().set(SoulShards.cageBornTag, true);

                if (spawned.isAlive() && !hasReachedSpawnCap(spawned) && level.noCollision(spawned)) {
                    if (!SoulShards.CONFIG_SERVER.getBalance().allowBossSpawns() && SoulShards.isBoss(spawned))
                        continue;

                    InteractionResult result = CageSpawnEvent.CAGE_SPAWN.invoker().onCageSpawn(binding,
                            inventory.getItem(0), spawned);
                    if (result == InteractionResult.FAIL)
                        continue spawnLoop;

                    if (spawned instanceof Mob) {
                        ((Mob) spawned).finalizeSpawn((ServerLevel) level,
                                level.getCurrentDifficultyAt(pos),
                                MobSpawnType.SPAWNER,
                                null,
                                null);
                    }
                    if (level instanceof ServerLevel lvl) {
                        if (!lvl.tryAddFreshEntityWithPassengers(spawned)) {
                            continue;
                        }
                        if (spawned instanceof Mob) {
                            ((Mob) spawned).spawnAnim();
                        }
                        break;
                    }
                }
            }
        }
    }


    public Binding getBinding() {
        ItemStack stack = inventory.getItem(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSoulShard))
            return null;

        return ((ItemSoulShard) stack.getItem()).getBinding(stack);
    }

    private boolean canSpawnInLight(LivingEntity entityLiving, BlockPos pos) {
        return !(entityLiving instanceof Monster) || getLevel().getBrightness(LightLayer.BLOCK, pos) <= 8;
    }

    private boolean hasReachedSpawnCap(LivingEntity living) {
        var pos = getBlockPos();
        var box = new AABB(pos.getX() - 16, pos.getY() - 16, pos.getZ() - 16, pos.getX() + 16,
                pos.getY() + 16, pos.getZ() + 16);

        int mobCount = getLevel().getEntitiesOfClass(living.getClass(), box,
                e -> e != null && e.getEntityData().get(SoulShards.cageBornTag)).size();
        return mobCount >= SoulShards.CONFIG_SERVER.getBalance().getSpawnCap();
    }

    public void setState(boolean active) {
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof BlockSoulCage))
            return;

        getLevel().setBlockAndUpdate(getBlockPos(), state.setValue(BlockSoulCage.ACTIVE, active));
        this.active = active;
    }

    public boolean ownerOnline() {
        Binding binding = getBinding();
        //noinspection ConstantConditions
        return binding != null && binding.getOwner() != null && getLevel().getServer()
                                                                          .getPlayerList()
                                                                          .getPlayer(binding.getOwner()) == null;
    }

    public Container getInventory() {
        return inventory;
    }
}
