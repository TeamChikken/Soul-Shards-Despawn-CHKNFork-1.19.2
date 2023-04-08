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
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.SoulSandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class ItemQuartzAndSteel extends FlintAndSteelItem {
    public ItemQuartzAndSteel() {
        super(new Properties().durability(64));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        var level = ctx.getLevel();
        var blockSt = level.getBlockState(ctx.getClickedPos());
        var player = ctx.getPlayer();
        var pos = ctx.getClickedPos().relative(ctx.getClickedFace());

        if (BaseFireBlock.canBePlacedAt(level, pos, ctx.getHorizontalDirection())) {
            BlockState fire;
            if (blockSt.getBlock() instanceof SoulSandBlock) {
                fire = RegistrarSoulShards.CURSED_FIRE.get().defaultBlockState();
            } else {
                fire = RegistrarSoulShards.HALLOWED_FIRE.get().getStateForFace(level, pos);
            }
            level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom()
                                                                                                       .nextFloat() * 0.4F + 0.8F);
            level.setBlock(pos, fire, 11);
            level.gameEvent(player, GameEvent.BLOCK_PLACE, ctx.getClickedPos());
            if (player instanceof ServerPlayer sp) {
                CriteriaTriggers.PLACED_BLOCK.trigger(sp, ctx.getClickedPos(), ctx.getItemInHand());
                var me = ctx.getItemInHand();
                me.hurtAndBreak(1, sp, px -> px.broadcastBreakEvent(ctx.getHand()));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.FAIL;
        }
    }
}
