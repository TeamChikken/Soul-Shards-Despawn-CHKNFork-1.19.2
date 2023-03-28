package info.n4tomic.soulshards;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class QuiltSoulShards implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        SoulShards.initCommon();
    }
}
