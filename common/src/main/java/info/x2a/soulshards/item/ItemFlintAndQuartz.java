package info.x2a.soulshards.item;

import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoulSandBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class ItemFlintAndQuartz extends FlintAndSteelItem {
    public ItemFlintAndQuartz() {
        super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        var level = ctx.getLevel();
        var blockSt = level.getBlockState(ctx.getClickedPos());
        var player = ctx.getPlayer();
        var pos = ctx.getClickedPos().relative(ctx.getClickedFace());

        if (blockSt.getBlock() instanceof SoulSandBlock) {
            level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom()
                                                                                                       .nextFloat() * 0.4F + 0.8F);
            level.setBlock(pos, RegistrarSoulShards.CURSED_FIRE.get().defaultBlockState(), 11);
            level.gameEvent(player, GameEvent.BLOCK_PLACE, pos);
            if (player instanceof ServerPlayer sp) {
                CriteriaTriggers.PLACED_BLOCK.trigger(sp, ctx.getClickedPos(), ctx.getItemInHand());
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.FAIL;
        }
    }
}
