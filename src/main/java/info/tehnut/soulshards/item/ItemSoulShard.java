package info.tehnut.soulshards.item;

import info.tehnut.soulshards.SoulShards;
import info.tehnut.soulshards.api.IShardTier;
import info.tehnut.soulshards.api.ISoulShard;
import info.tehnut.soulshards.block.TileEntitySoulCage;
import info.tehnut.soulshards.core.RegistrarSoulShards;
import info.tehnut.soulshards.core.data.Binding;
import info.tehnut.soulshards.core.data.Tier;
import info.tehnut.soulshards.core.mixin.MobSpawnerLogicEntityId;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.InteractionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;

import java.util.List;

public class ItemSoulShard extends Item implements ISoulShard {

    public ItemSoulShard() {
        super(new Properties().stacksTo(1).tab(QuiltItemGroup.TAB_MISC));
         (new ResourceLocation(SoulShards.MODID, "bound"),
                (stack, worldIn, entityIn) -> getBinding(stack) != null ? 1.0F : 0.0F);
        addPropertyGetter(new Identifier(SoulShards.MODID, "tier"), (stack, world, entity) -> {
            Binding binding = getBinding(stack);
            if (binding == null)
                return 0F;

            return Float.parseFloat("0." + Tier.INDEXED.indexOf(binding.getTier()));
        });
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

            ItemStack cageStack = cage.getInventory().getInvStack(0);
            if (cageStack.isEmpty() && cage.getInventory().isValidInvStack(0, context.getStack())) {
                cage.getInventory().setInvStack(0, context.getStack().copy());
                context.getStack().decrement(1);
                cage.markDirty();
                cage.setState(true);
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext options) {
        Binding binding = getBinding(stack);
        if (binding == null)
            return;

        Style greyColor = new Style().setColor(Formatting.GRAY);
        if (binding.getBoundEntity() != null) {
            EntityType entityEntry = Registry.ENTITY_TYPE.get(binding.getBoundEntity());
            if (entityEntry != null)
                tooltip.add(new TranslatableText("tooltip.soulshards.bound", entityEntry.getName()).setStyle(greyColor));
            else
                tooltip.add(new TranslatableText("tooltip.soulshards.bound", binding.getBoundEntity().toString()).setStyle(new Style().setColor(Formatting.RED)));
        }

        tooltip.add(new TranslatableText("tooltip.soulshards.tier", binding.getTier().getIndex()).setStyle(greyColor));
        tooltip.add(new TranslatableText("tooltip.soulshards.kills", binding.getKills()).setStyle(greyColor));
        if (options.isAdvanced() && binding.getOwner() != null)
            tooltip.add(new TranslatableText("tooltip.soulshards.owner", binding.getOwner().toString()).setStyle(greyColor));
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
        if (!isIn(group))
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
    public String getTranslationKey(ItemStack stack) {
        Binding binding = getBinding(stack);
        return super.getTranslationKey(stack) + (binding == null || binding.getBoundEntity() == null ? "_unbound" : "");
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack stack) {
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
