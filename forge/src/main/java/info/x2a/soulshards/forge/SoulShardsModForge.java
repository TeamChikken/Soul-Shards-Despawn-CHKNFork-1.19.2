package info.x2a.soulshards.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.compat.clothconfig.SoulShardsConfigScreen;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(SoulShards.MODID)
public class SoulShardsModForge {
    public SoulShardsModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SoulShards.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener((FMLClientSetupEvent ev) -> {
            RenderTypeRegistry.register(RenderType.cutout(), RegistrarSoulShards.SOUL_CAGE.get());
        });
        SoulShards.initCommon();
        if (SoulShards.IS_CLOTH_CONFIG_LOADED) {
            ModLoadingContext.get()
                             .registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new SoulShardsConfigScreen(screen).screen()));
        }

    }
}
