package info.x2a.soulshards.fabriclike;


import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class SoulShardsModFabricLike {
    public static void initServer() {
        SoulShards.initCommon();
    }

    public static void initClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(RegistrarSoulShards.SOUL_CAGE.get(), RenderType.cutout());
        SoulShards.afterLoad();
    }
}
