package info.x2a.soulshards.quilt;

import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.fabriclike.SoulShardsModFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class QuiltSoulShards implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        SoulShardsModFabricLike.initServer();
    }
}
