package info.n4tomic.soulshards.core.mixin;

import info.n4tomic.soulshards.SoulShards;
import info.n4tomic.soulshards.core.RegistrarSoulShards;
import info.n4tomic.soulshards.core.data.Binding;
import info.n4tomic.soulshards.item.ItemSoulShard;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class MixinAnvilContainer {

    @Shadow
    @Final
    private DataSlot cost;


    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    public void soulshards$createResult(CallbackInfo callbackInfo) {
        if (!SoulShards.CONFIG.getBalance().allowShardCombination())
            return;

        var slots = ((AbstractContainerMenu)(Object)this).slots;
        ItemStack leftStack = slots.get(ItemCombinerMenu.INPUT_SLOT).getItem();
        ItemStack rightStack = slots.get(ItemCombinerMenu.ADDITIONAL_SLOT).getItem();

        if (leftStack.getItem() instanceof ItemSoulShard && rightStack.getItem() instanceof ItemSoulShard) {
            Binding left = ((ItemSoulShard) leftStack.getItem()).getBinding(leftStack);
            Binding right = ((ItemSoulShard) rightStack.getItem()).getBinding(rightStack);

            if (left == null || right == null)
                return;

            if (left.getBoundEntity() != null && left.getBoundEntity().equals(right.getBoundEntity())) {
                ItemStack output = new ItemStack(RegistrarSoulShards.SOUL_SHARD);
                ((ItemSoulShard) output.getItem()).updateBinding(output, left.addKills(right.getKills()));
                slots.get(ItemCombinerMenu.RESULT_SLOT).set(output);
                cost.set(left.getTier().getIndex() * 6);
                callbackInfo.cancel();
            }
        }
    }
}
