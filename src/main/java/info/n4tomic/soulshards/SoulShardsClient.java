package info.n4tomic.soulshards;

import info.n4tomic.soulshards.core.RegistrarSoulShards;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.impl.util.log.Log;
import org.quiltmc.loader.impl.util.log.LogCategory;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class SoulShardsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer container) {
        BlockRenderLayerMap.INSTANCE.putBlock(RegistrarSoulShards.SOUL_CAGE, RenderType.cutout());
        Log.info(LogCategory.GENERAL, "Soul Shards Resewn rises once again");
    }
}
