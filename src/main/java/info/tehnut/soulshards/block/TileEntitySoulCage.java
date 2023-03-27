package info.tehnut.soulshards.block;

import info.tehnut.soulshards.SoulShards;
import info.tehnut.soulshards.api.CageSpawnEvent;
import info.tehnut.soulshards.api.IShardTier;
import info.tehnut.soulshards.core.RegistrarSoulShards;
import info.tehnut.soulshards.core.data.Binding;
import info.tehnut.soulshards.item.ItemSoulShard;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class TileEntitySoulCage extends BlockEntity implements Tickable {

    private Container inventory;
    private boolean active;

    public TileEntitySoulCage(BlockPos pos, BlockState state) {
        super(RegistrarSoulShards.SOUL_CAGE_TE, pos, state);

        this.inventory = new SimpleContainer(1) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                if (!(stack.getItem() instanceof ItemSoulShard))
                    return false;

                Binding binding = ((ItemSoulShard) stack.getItem()).getBinding(stack);
                return binding != null && binding.getBoundEntity() != null && SoulShards.CONFIG.getEntityList().isEnabled(binding.getBoundEntity());
            }
        };
    }

    private InteractionResultHolder<Binding> canSpawn(Level level, BlockPos pos) {
        // TODO mojang pls
//        if (!getWorld().getServer().getWorld(DimensionType.OVERWORLD).getGameRules().getBoolean(SoulShards.allowCageSpawns))
//            return new InteractionResultHolder<>(ActionResult.FAIL, null);

        BlockState state = level.getBlockState(pos);
        if (state.getBlock() != RegistrarSoulShards.SOUL_CAGE)
            return new InteractionResultHolder<>(InteractionResult.FAIL, null);

        ItemStack shardStack = inventory.getItem(0);
        if (shardStack.isEmpty() || !(shardStack.getItem() instanceof ItemSoulShard))
            return new InteractionResultHolder<>(InteractionResult.FAIL, null);

        Binding binding = ((ItemSoulShard) shardStack.getItem()).getBinding(shardStack);
        if (binding == null || binding.getBoundEntity() == null)
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        IShardTier tier = binding.getTier();
        if (tier.getSpawnAmount() == 0)
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        if (SoulShards.CONFIG.getBalance().requireOwnerOnline() && !ownerOnline())
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        if (!SoulShards.CONFIG.getEntityList().isEnabled(binding.getBoundEntity()))
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        if (!SoulShards.CONFIG.getBalance().requireRedstoneSignal()) {
            if (state.getValue(BlockSoulCage.POWERED) && tier.checkRedstone())
                return new InteractionResultHolder<>(InteractionResult.FAIL, binding);
        } else if (!state.getValue(BlockSoulCage.POWERED))
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        if (tier.checkPlayer() && level.getNearestPlayer(pos.getX(), pos.getY(), pos.getX(), 16,
                false) == null)
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, binding);
    }
    @Override
    public void tick() {
        if (level == null || level.isClientSide)
            return;

        var pos = getBlockPos();
        var level = getLevel();
        assert level != null;
        InteractionResultHolder<Binding> result = canSpawn(level, pos);
        if (result.getResult() != InteractionResult.SUCCESS) {
            if (active) {
                setState(false);
                level.updateNeighborsAt(pos, getBlockState().getBlock());
            }
            return;
        }

        if (!active) {
            setState(true);
            level.updateNeighborsAt(pos, getBlockState().getBlock());
        }

        if (level.getGameTime() % result.getObject().getTier().getCooldown() == 0)
            spawnEntities();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("shard"))
            inventory.setItem(0, ItemStack.of(tag.getCompound("shard")));
        this.active = tag.getBoolean("active");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        ItemStack shardStack = inventory.getItem(0);
        if (!shardStack.isEmpty())
            tag.put("shard", shardStack.save(new CompoundTag()));
        tag.putBoolean("active", active);
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

                if (spawned.isAlive() && !hasReachedSpawnCap(spawned) && !isColliding(spawned)) {
                    if (!SoulShards.CONFIG.getBalance().allowBossSpawns() && !spawned.getTags().contains(SoulShards.BOSS_TAG))
                        continue;

                    InteractionResult result = CageSpawnEvent.CAGE_SPAWN.invoker().onCageSpawn(binding,
                            inventory.getItem(0), spawned);
                    if (result == InteractionResult.FAIL)
                        continue spawnLoop;

                    getLevel().addFreshEntity(spawned);
                    if (spawned instanceof Mob)
                        ((Mob) spawned).finalizeSpawn((ServerLevel)level,
                                level.getCurrentDifficultyAt(pos),
                                MobSpawnType.SPAWNER,
                                null,
                                null);
                    break;
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
        return mobCount >= SoulShards.CONFIG.getBalance().getSpawnCap();
    }

    private boolean isColliding(LivingEntity entity) {
        return !getLevel().noCollision(entity, entity.getBoundingBox()) || getLevel().getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox(), e -> true).isEmpty();
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
        return binding != null && binding.getOwner() != null && getLevel().getServer().getPlayerList().getPlayer(binding.getOwner()) == null;
    }

    public Container getInventory() {
        return inventory;
    }
}
