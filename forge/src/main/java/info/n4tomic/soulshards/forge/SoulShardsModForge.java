package info.n4tomic.soulshards.forge;

import dev.architectury.platform.forge.EventBuses;
import info.n4tomic.soulshards.SoulShards;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SoulShards.MODID)
public class SoulShardsModForge {
    public SoulShardsModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SoulShards.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        SoulShards.initCommon();
    }
}
