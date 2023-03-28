package info.n4tomic.soulshards.quilt;

import info.n4tomic.soulshards.SoulShards;
import info.n4tomic.soulshards.core.registry.RegistrarSoulShards;
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
