package info.x2a.soulshards.item;

import dev.architectury.registry.CreativeTabRegistry;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.api.IShardTier;
import info.x2a.soulshards.api.ISoulShard;
import info.x2a.soulshards.block.TileEntitySoulCage;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import info.x2a.soulshards.core.data.Binding;
import info.x2a.soulshards.core.data.Tier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemSoulShard extends Item implements ISoulShard {

    public ItemSoulShard() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x9F63ED;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        if (!SoulShards.CONFIG_CLIENT.displayDurabilityBar()) {
            return false;
        }
        var binding = getBinding(stack);
        return binding != null && binding.getKills() < Tier.maxKills;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var maxPx = 13F;
        var binding = getBinding(stack);
        var current = (float) (binding != null ? binding.getKills() : 0);
        var max = (float) Tier.maxKills;
        var percentage = current / max;
        return Math.round(maxPx * percentage);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        var state = context.getLevel().getBlockState(context.getClickedPos());
        var binding = getBinding(context.getItemInHand());
        if (binding == null) {
            return InteractionResult.PASS;
        }

        if (state.getBlock() instanceof SpawnerBlock) {
            if (!SoulShards.CONFIG_SERVER.getBalance().allowSpawnerAbsorption()) {
                if (context.getPlayer() != null)
                    context.getPlayer().displayClientMessage(SoulShards.translate("chat" +
                                    ".soulshards" +
                                    ".absorb_disabled"),
                            true);
                return InteractionResult.PASS;
            }

            if (binding.getKills() > Tier.maxKills)
                return InteractionResult.PASS;

            var spawner = (SpawnerBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
            if (spawner == null) {
                SoulShards.Log.warn("Failed to get spawner entity at pos {}", context.getClickedPos().toString());
                return InteractionResult.PASS;
            }

            try {
                ResourceLocation entityId =
                        EntityType.getKey(spawner.getSpawner()
                                                 .getOrCreateDisplayEntity(context.getLevel(),
                                                         context.getLevel().random, context.getClickedPos())
                                                 .getType());
                if (!SoulShards.CONFIG_SERVER.getEntityList().isEnabled(entityId)) {
                    SoulShards.Log.debug("Tried to consume entity which is disallowed in the " +
                                    "config: {}",
                            entityId.toString());
                    return InteractionResult.PASS;
                }

                if (binding.getBoundEntity() == null) {
                    binding.setBoundEntity(entityId);
                } else if (!binding.getBoundEntity().equals(entityId)) {
                    SoulShards.Log.warn("Tried to consume entity that doesn't match bound: {} vs" +
                                    " {}",
                            binding.getBoundEntity().toString(), binding.getBoundEntity().toString());
                    return InteractionResult.FAIL;
                }

                updateBinding(context.getItemInHand(), binding.addKills(SoulShards.CONFIG_SERVER.getBalance()
                                                                                                .getAbsorptionBonus()));
                context.getLevel().destroyBlock(context.getClickedPos(), false);
                return InteractionResult.SUCCESS;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else if (state.getBlock() == RegistrarSoulShards.SOUL_CAGE.get()) {
            if (binding.getBoundEntity() == null)
                return InteractionResult.FAIL;

            TileEntitySoulCage cage = (TileEntitySoulCage) context.getLevel().getBlockEntity(context.getClickedPos());
            if (cage == null)
                return InteractionResult.PASS;

            ItemStack cageStack = cage.getInventory().getItem(0);
            if (cageStack.isEmpty() && cage.getInventory().canPlaceItem(0, context.getItemInHand())) {
                cage.setShard(context.getItemInHand().copy());
                context.getItemInHand().shrink(1);
                cage.setChanged();
                cage.setState(true);
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip,
                                TooltipFlag options) {
        Binding binding = getBinding(stack);
        if (binding == null)
            return;

        var greyColor = Style.EMPTY.withFont(Style.DEFAULT_FONT).withColor(ChatFormatting.GRAY);
        if (binding.getBoundEntity() != null) {
            var opt = BuiltInRegistries.ENTITY_TYPE.getOptional(binding.getBoundEntity());
            if (opt.isPresent()) {
                tooltip.add(Component.translatable("tooltip.soulshards.bound",
                        opt.get().getDescription()).withStyle(greyColor));
            } else {
                tooltip.add(Component.translatable("tooltip.soulshards.bound",
                        binding.getBoundEntity().toString()).setStyle(greyColor.withColor(ChatFormatting.RED)));
            }
        }

        tooltip.add(Component.translatable("tooltip.soulshards.tier",
                binding.getTier().getIndex()).withStyle(greyColor));
        tooltip.add(Component.translatable("tooltip.soulshards.kills", binding.getKills())
                             .setStyle(greyColor));
        if (options.isAdvanced()) {
            if (binding.getOwner() != null) {
                tooltip.add(Component.translatable("tooltip.soulshards.owner",
                        binding.getOwner().toString()).withStyle(ChatFormatting.AQUA));
            }
        }
    }

    @Override
    public @NotNull String getDescriptionId(ItemStack stack) {
        Binding binding = getBinding(stack);
        return super.getDescriptionId(stack) + (binding == null || binding.getBoundEntity() == null ? "_unbound" : "");
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        Binding binding = getBinding(stack);
        return binding != null && binding.getKills() >= Tier.maxKills;
    }

    @Override
    @Nullable
    public Binding getBinding(ItemStack stack) {
        return Binding.fromNBT(stack);
    }

    public void updateBinding(ItemStack stack, Binding binding) {
        CompoundTag tag = stack.getTag();
        if (tag == null)
            stack.setTag(tag = new CompoundTag());

        tag.put("binding", binding.serializeNBT());
    }
}
