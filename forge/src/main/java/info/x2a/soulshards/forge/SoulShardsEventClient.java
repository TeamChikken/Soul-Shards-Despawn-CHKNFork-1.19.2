package info.x2a.soulshards.forge;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.compat.clothconfig.SoulShardsConfigScreen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = SoulShards.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SoulShardsEventClient {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(RegisterClientCommandsEvent ev) {
        if (SoulShards.IS_CLOTH_CONFIG_LOADED) {
            ev.getDispatcher()
              .register(LiteralArgumentBuilder.<CommandSourceStack>literal("soulshards")
                                              .then(LiteralArgumentBuilder.<CommandSourceStack>literal("config")
                                                                          .executes(context -> {
                                                                              SoulShardsConfigScreen.popup();
                                                                              return 1;
                                                                          })));
        }
    }
}
