package info.x2a.soulshards.forge;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.platform.forge.EventBuses;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.compat.clothconfig.SoulShardsConfigScreen;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.ClientCommandSourceStack;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

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
    }
}
