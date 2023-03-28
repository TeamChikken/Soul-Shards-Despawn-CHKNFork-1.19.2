package info.n4tomic.soulshards.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import info.n4tomic.soulshards.SoulShards;
import info.n4tomic.soulshards.core.registry.RegistrarSoulShards;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SoulShards.MODID)
public class SoulShardsModForge {
    public SoulShardsModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SoulShards.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent ev) -> {
            RenderTypeRegistry.register(RenderType.cutout(), RegistrarSoulShards.SOUL_CAGE.get());
        });
        SoulShards.initCommon();
    }
}
