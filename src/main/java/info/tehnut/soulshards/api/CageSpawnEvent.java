package info.tehnut.soulshards.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface CageSpawnEvent {

    Event<CageSpawnEvent> CAGE_SPAWN = EventFactory.createArrayBacked(CageSpawnEvent.class,
            (listeners) -> (binding, shardStack, toSpawn) -> {
                for (CageSpawnEvent event : listeners) {
                    InteractionResult result = event.onCageSpawn(binding, shardStack, toSpawn);
                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            }
    );

    InteractionResult onCageSpawn(IBinding binding, ItemStack shardStack, LivingEntity toSpawn);
}
