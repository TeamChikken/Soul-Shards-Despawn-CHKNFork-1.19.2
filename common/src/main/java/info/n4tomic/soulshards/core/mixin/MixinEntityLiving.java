package info.n4tomic.soulshards.core.mixin;

import info.n4tomic.soulshards.SoulShards;
import info.n4tomic.soulshards.core.EventHandler;

import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.quiltmc.loader.impl.util.log.Log;
import org.quiltmc.loader.impl.util.log.LogCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinEntityLiving {

    @Shadow
    @Final
    private boolean dead;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void registerDataTracker(CallbackInfo callbackInfo) {
        SoulShards.cageBornTag = SynchedEntityData.defineId(LivingEntity.class,
            EntityDataSerializers.BOOLEAN);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void initDataTracker(CallbackInfo callbackInfo) {
        try {
            LivingEntity entity = (LivingEntity) (Object) this;
            if (entity instanceof Player)
                return;
            entity.getEntityData().define(SoulShards.cageBornTag, false);
        } catch(Exception e) {
            Log.error(LogCategory.GENERAL, "during synched data: %s", e.getMessage());
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void onDeathEvent(DamageSource damageSource, CallbackInfo callbackInfo) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player || dead)
            return;

        EventHandler.onEntityDeath(entity, damageSource);
    }
}
