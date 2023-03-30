package info.x2a.soulshards.fabric;

import info.x2a.soulshards.fabriclike.SoulShardsModFabricLike;
import net.fabricmc.api.ClientModInitializer;

public class SoulShardsModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SoulShardsModFabricLike.initClient();
    }
}
