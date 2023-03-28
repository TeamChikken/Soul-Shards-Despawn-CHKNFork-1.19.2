package info.n4tomic.soulshards.core.mixin;

import info.n4tomic.soulshards.SoulShards;
import info.n4tomic.soulshards.core.EventHandler;

import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinEntityLiving {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void registerDataTracker(CallbackInfo callbackInfo) {
        SoulShards.cageBornTag = SynchedEntityData.defineId(LivingEntity.class,
            EntityDataSerializers.BOOLEAN);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTracker(CallbackInfo callbackInfo) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player)
            return;
        entity.getEntityData().set(SoulShards.cageBornTag, false);
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void onDeathEvent(DamageSource source, CallbackInfo callbackInfo) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player)
            return;

        EventHandler.onEntityDeath(entity, source);
    }
}
