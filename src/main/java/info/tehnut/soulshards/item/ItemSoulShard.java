package info.tehnut.soulshards.item;

import info.tehnut.soulshards.SoulShards;
import info.tehnut.soulshards.api.IShardTier;
import info.tehnut.soulshards.api.ISoulShard;
import info.tehnut.soulshards.block.TileEntitySoulCage;
import info.tehnut.soulshards.core.RegistrarSoulShards;
import info.tehnut.soulshards.core.data.Binding;
import info.tehnut.soulshards.core.data.Tier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;

import java.util.List;

public class ItemSoulShard extends Item implements ISoulShard {

    public ItemSoulShard() {
        super(new Properties().stacksTo(1).tab(QuiltItemGroup.TAB_MISC));
        // TODO: migrate me
        /*
         appendStacks(new ResourceLocation(SoulShards.MODID, "bound"),
                (stack, worldIn, entityIn) -> getBinding(stack) != null ? 1.0F : 0.0F);
        addPropertyGetter(new ResourceLocation(SoulShards.MODID, "tier"), (stack, world, entity) -> {
            Binding binding = getBinding(stack);
            if (binding == null)
                return 0F;

            return Float.parseFloat("0." + Tier.INDEXED.indexOf(binding.getTier()));
        });*/
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var state = context.getLevel().getBlockState(context.getClickedPos());
        var binding = getBinding(context.getItemInHand());
        if (binding == null)
            return InteractionResult.PASS;

        if (state.getBlock() instanceof SpawnerBlock) {
            if (!SoulShards.CONFIG.getBalance().allowSpawnerAbsorption()) {
                if (context.getPlayer() != null)
                    context.getPlayer().displayClientMessage(MutableComponent.create(new TranslatableContents("chat" +
                                    ".soulshards" +
                                    ".absorb_disabled")),
                            true);
                return InteractionResult.PASS;
            }

            if (binding.getKills() > Tier.maxKills)
                return InteractionResult.PASS;

            var spawner = (SpawnerBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
            if (spawner == null)
                return InteractionResult.PASS;

            try {
                ResourceLocation entityId = EntityType.getKey(spawner.getSpawner().getOrCreateDisplayEntity(context.getLevel()).getType());
                if (!SoulShards.CONFIG.getEntityList().isEnabled(entityId))
                    return InteractionResult.PASS;

                if (binding.getBoundEntity() == null || !binding.getBoundEntity().equals(entityId))
                    return InteractionResult.FAIL;

                updateBinding(context.getItemInHand(), binding.addKills(SoulShards.CONFIG.getBalance().getAbsorptionBonus()));
                context.getLevel().destroyBlock(context.getClickedPos(), false);
                return InteractionResult.SUCCESS;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else if (state.getBlock() == RegistrarSoulShards.SOUL_CAGE) {
            if (binding.getBoundEntity() == null)
                return InteractionResult.FAIL;

            TileEntitySoulCage cage = (TileEntitySoulCage) context.getLevel().getBlockEntity(context.getClickedPos());
            if (cage == null)
                return InteractionResult.PASS;

            ItemStack cageStack = cage.getInventory().getItem(0);
            if (cageStack.isEmpty() && cage.getInventory().canPlaceItem(0, context.getItemInHand())) {
                cage.getInventory().setItem(0, context.getItemInHand().copy());
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
            var entityEntry = Registry.ENTITY_TYPE.get(binding.getBoundEntity());
            if (entityEntry != null)
                tooltip.add(MutableComponent.create(new TranslatableContents("tooltip.soulshards.bound",
                        entityEntry.getDescription())).withStyle(greyColor));
            else
                tooltip.add(MutableComponent.create(new TranslatableContents("tooltip.soulshards.bound",
                        binding.getBoundEntity().toString())).setStyle(greyColor.withColor(ChatFormatting.RED)));
        }

        tooltip.add(MutableComponent.create(new TranslatableContents("tooltip.soulshards.tier",
                binding.getTier().getIndex())).withStyle(greyColor));
        tooltip.add(MutableComponent.create(new TranslatableContents("tooltip.soulshards.kills", binding.getKills())).setStyle(greyColor));
        if (options.isAdvanced() && binding.getOwner() != null)
            tooltip.add(MutableComponent.create(new TranslatableContents("tooltip.soulshards.owner",
                    binding.getOwner().toString())).setStyle(greyColor));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (!allowedIn(group))
            return;

        items.add(new ItemStack(this));
        for (IShardTier tier : Tier.INDEXED) {
            ItemStack stack = new ItemStack(this);
            Binding binding = new Binding(null, tier.getKillRequirement());
            updateBinding(stack, binding);
            items.add(stack);
        }
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        Binding binding = getBinding(stack);
        return super.getDescriptionId(stack) + (binding == null || binding.getBoundEntity() == null ? "_unbound" : "");
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        Binding binding = getBinding(stack);
        return binding != null && binding.getKills() >= Tier.maxKills;
    }

    @Override
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
