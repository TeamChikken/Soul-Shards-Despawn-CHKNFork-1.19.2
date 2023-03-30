package info.x2a.soulshards.core.mixin;

import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.core.EventHandler;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import info.x2a.soulshards.core.data.Binding;
import info.x2a.soulshards.item.ItemSoulShard;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
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
        var slots = ((AbstractContainerMenu) (Object) this).slots;
        ItemStack leftStack = slots.get(0).getItem();
        ItemStack rightStack = slots.get(1).getItem();
        EventHandler.onAnvilCraft(leftStack, rightStack, output -> {
            slots.get(2).set(output);
            callbackInfo.cancel();
        }, costOut -> {
            cost.set(costOut);
        });
    }
}
