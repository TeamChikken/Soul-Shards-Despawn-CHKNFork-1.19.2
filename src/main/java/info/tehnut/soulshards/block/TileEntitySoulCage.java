package info.tehnut.soulshards.block;

import info.tehnut.soulshards.SoulShards;
import info.tehnut.soulshards.api.CageSpawnEvent;
import info.tehnut.soulshards.api.IShardTier;
import info.tehnut.soulshards.core.RegistrarSoulShards;
import info.tehnut.soulshards.core.data.Binding;
import info.tehnut.soulshards.item.ItemSoulShard;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnType;
import net.minecraft.world.entity.mob.MobEntity;
import net.minecraft.world.entity.mob.Monster;
import net.minecraft.world.inventory.BasicInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;

public class TileEntitySoulCage extends BlockEntity implements Tickable {

    private Container inventory;
    private boolean active;

    public TileEntitySoulCage(BlockPos pos, BlockState state) {
        super(RegistrarSoulShards.SOUL_CAGE_TE, pos, state);

        this.inventory = new SimpleContainer(1) {
            @Override
            public boolean canAddItem(ItemStack stack) {
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

        InteractionResultHolder<Binding> result = canSpawn();
        if (result.getResult() != InteractionResult.SUCCESS) {
            if (active) {
                setState(false);
                getWorld().updateNeighbors(pos, getCachedState().getBlock());
            }
            return;
        }

        if (!active) {
            setState(true);
            getWorld().updateNeighbors(pos, getCachedState().getBlock());
        }

        if (getWorld().getTime() % result.getValue().getTier().getCooldown() == 0)
            spawnEntities();
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        if (tag.contains("shard"))
            inventory.setInvStack(0, ItemStack.fromTag(tag.getCompound("shard")));
        this.active = tag.getBoolean("active");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        ItemStack shardStack = inventory.getInvStack(0);
        if (!shardStack.isEmpty())
            tag.put("shard", shardStack.toTag(new CompoundTag()));
        tag.putBoolean("active", active);

        return super.toTag(tag);
    }

    private void spawnEntities() {
        Binding binding = getBinding();
        if (binding == null || binding.getBoundEntity() == null)
            return;

        EntityType entityType = Registry.ENTITY_TYPE.get(binding.getBoundEntity());
        IShardTier tier = binding.getTier();
        spawnLoop:
        for (int i = 0; i < tier.getSpawnAmount(); i++) {
            for (int attempts = 0; attempts < 5; attempts++) {

                double x = getPos().getX() + (getWorld().random.nextDouble() - getWorld().random.nextDouble()) * 4.0D;
                double y = getPos().getY() + getWorld().random.nextInt(3) - 1;
                double z = getPos().getZ() + (getWorld().random.nextDouble() - getWorld().random.nextDouble()) * 4.0D;
                BlockPos spawnAt = new BlockPos(x, y, z);

                LivingEntity entityLiving = (LivingEntity) entityType.create(getWorld());
                if (entityLiving == null)
                    continue;

                if (tier.checkLight() && !canSpawnInLight(entityLiving, spawnAt))
                    continue;

                entityLiving.refreshPositionAndAngles(spawnAt, getWorld().random.nextFloat() * 360F, 0F);
                entityLiving.getDataTracker().set(SoulShards.cageBornTag, true);

                if (entityLiving.isAlive() && !hasReachedSpawnCap(entityLiving) && !isColliding(entityLiving)) {
                    if (!SoulShards.CONFIG.getBalance().allowBossSpawns() && !entityLiving.canUsePortals()) // canUsePortals -> isNonBoss
                        continue;

                    InteractionResult result = CageSpawnEvent.CAGE_SPAWN.invoker().onCageSpawn(binding, inventory.getInvStack(0), entityLiving);
                    if (result == InteractionResult.FAIL)
                        continue spawnLoop;

                    getWorld().spawnEntity(entityLiving);
                    if (entityLiving instanceof MobEntity)
                        ((MobEntity) entityLiving).initialize(world, getWorld().getLocalDifficulty(pos), SpawnType.SPAWNER, null, null);
                    break;
                }
            }
        }
    }


    public Binding getBinding() {
        ItemStack stack = inventory.getInvStack(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSoulShard))
            return null;

        return ((ItemSoulShard) stack.getItem()).getBinding(stack);
    }

    private boolean canSpawnInLight(LivingEntity entityLiving, BlockPos pos) {
        return !(entityLiving instanceof Monster) || getWorld().getLightLevel(LightType.BLOCK, pos) <= 8;
    }

    private boolean hasReachedSpawnCap(LivingEntity living) {
        Box box = new Box(getPos().getX() - 16, getPos().getY() - 16, getPos().getZ() - 16, getPos().getX() + 16, getPos().getY() + 16, getPos().getZ() + 16);

        int mobCount = getWorld().getEntities(living.getClass(), box, e -> e != null && e.getDataTracker().get(SoulShards.cageBornTag)).size();
        return mobCount >= SoulShards.CONFIG.getBalance().getSpawnCap();
    }

    private boolean isColliding(LivingEntity entity) {
        return getWorld().isAreaNotEmpty(entity.getBoundingBox()) && getWorld().getEntities(LivingEntity.class, entity.getBoundingBox(), e -> true).isEmpty();
    }

    public void setState(boolean active) {
        BlockState state = getCachedState();
        if (!(state.getBlock() instanceof BlockSoulCage))
            return;

        getWorld().setBlockState(getPos(), state.with(BlockSoulCage.ACTIVE, active));
        this.active = active;
    }

    public boolean ownerOnline() {
        Binding binding = getBinding();
        //noinspection ConstantConditions
        return binding != null && binding.getOwner() != null && getWorld().getServer().getPlayerManager().getPlayer(binding.getOwner()) == null;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
