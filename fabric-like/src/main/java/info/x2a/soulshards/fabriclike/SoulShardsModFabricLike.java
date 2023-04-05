package info.x2a.soulshards.fabriclike;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.compat.clothconfig.SoulShardsConfigScreen;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.CommandBuildContext;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class SoulShardsModFabricLike {
    public static void initServer() {
        SoulShards.initCommon();
    }

    public static void initClient() {
        SoulShards.afterLoad();
        if (SoulShards.IS_CLOTH_CONFIG_LOADED) {
            ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) -> {
                dispatcher.register(literal("soulshards").then(literal("config").executes((context) -> {
                    SoulShardsConfigScreen.popup();
                    return 1;
                })));
            });
        }
        BlockRenderLayerMap.INSTANCE.putBlock(RegistrarSoulShards.SOUL_CAGE.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegistrarSoulShards.CURSED_FIRE.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegistrarSoulShards.HALLOWED_FIRE.get(), RenderType.cutout());
    }
}
