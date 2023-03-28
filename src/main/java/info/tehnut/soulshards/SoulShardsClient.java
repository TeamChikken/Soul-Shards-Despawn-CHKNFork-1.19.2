package info.tehnut.soulshards;

import com.mojang.blaze3d.systems.RenderSystem;
import info.tehnut.soulshards.core.RegistrarSoulShards;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class SoulShardsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(RegistrarSoulShards.SOUL_CAGE, RenderType.cutout());
    }
}
