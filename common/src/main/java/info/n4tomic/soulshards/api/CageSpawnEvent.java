package info.n4tomic.soulshards.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface CageSpawnEvent {

    Event<CageSpawnEvent> CAGE_SPAWN = EventFactory.of(
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
