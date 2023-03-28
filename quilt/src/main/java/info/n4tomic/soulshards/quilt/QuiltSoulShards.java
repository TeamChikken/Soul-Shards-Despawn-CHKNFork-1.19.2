package info.n4tomic.soulshards.quilt;

import info.n4tomic.soulshards.SoulShards;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class QuiltSoulShards implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        SoulShards.initCommon();
    }
}
