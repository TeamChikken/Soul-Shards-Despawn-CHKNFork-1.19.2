package info.x2a.soulshards.fabric;

import info.x2a.soulshards.fabriclike.SoulShardsModFabricLike;
import net.fabricmc.api.ModInitializer;

public class SoulShardsModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SoulShardsModFabricLike.initServer();
    }
}
