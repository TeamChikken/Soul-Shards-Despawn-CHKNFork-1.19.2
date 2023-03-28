package info.n4tomic.soulshards.quilt;

import info.n4tomic.soulshards.SoulShards;
import info.n4tomic.soulshards.core.RegistrarSoulShards;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.impl.util.log.Log;
import org.quiltmc.loader.impl.util.log.LogCategory;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class QuiltSoulShardsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer container) {
        //BlockRenderLayerMap.INSTANCE.putBlock(RegistrarSoulShards.SOUL_CAGE, RenderType.cutout());

        SoulShards.afterLoad();
    }
}
