package info.x2a.soulshards.compat.waila;

import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.block.TileEntitySoulCage;
import info.x2a.soulshards.core.data.Binding;
import mcp.mobius.waila.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SoulShardsWailaPlugin implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(new IEntityComponentProvider() {
            @Override
            public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
                if (!(accessor.getEntity() instanceof RemotePlayer) && accessor.getEntity()
                                                                               .getEntityData()
                                                                               .get(SoulShards.cageBornTag)) {
                    tooltip.addLine(Component.translatable("tooltip.soulshards.cage_born"));
                }
            }
        }, TooltipPosition.BODY, LivingEntity.class);

        registrar.addBlockData(new IDataProvider<BlockEntity>() {
            @Override
            public void appendData(IDataWriter block, IServerAccessor<BlockEntity> server, IPluginConfig config) {
                var binding = ((TileEntitySoulCage) server.getTarget()).getBinding();
                if (binding != null)
                    block.raw().put("binding", binding.serializeNBT());
            }
        }, TileEntitySoulCage.class);

        registrar.addComponent(new IBlockComponentProvider() {
            @Override
            public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
                if (!accessor.getData().raw().contains("binding"))
                    return;

                Binding binding = new Binding(accessor.getData().raw().getCompound("binding"));

                if (binding.getBoundEntity() != null) {
                    var opt = BuiltInRegistries.ENTITY_TYPE.getOptional(binding.getBoundEntity());
                    if (opt.isPresent()) {
                        tooltip.addLine(Component.translatable("tooltip.soulshards.bound",
                                opt.get().getDescription()));
                    } else {
                        tooltip.addLine(Component.translatable("tooltip.soulshards.bound",
                                                         binding.getBoundEntity().toString())
                                                 .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                    }
                }

                tooltip.addLine(Component.translatable("tooltip.soulshards.tier",
                        binding.getTier().getIndex()));
            }
        }, TooltipPosition.BODY, TileEntitySoulCage.class);
    }
}
