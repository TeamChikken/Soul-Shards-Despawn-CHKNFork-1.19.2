package info.x2a.soulshards.quilt;

import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class QuiltSoulShardsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer container) {
        BlockRenderLayerMap.INSTANCE.putBlock(RegistrarSoulShards.SOUL_CAGE.get(), RenderType.cutout());
        SoulShards.afterLoad();
    }
}
