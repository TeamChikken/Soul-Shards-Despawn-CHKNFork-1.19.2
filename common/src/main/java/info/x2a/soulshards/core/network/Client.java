package info.x2a.soulshards.core.network;

import info.x2a.soulshards.core.ConfigSoulShards;
import info.x2a.soulshards.core.network.message.ConfigUpdate;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

public class Client {
    public static void sendConfig(ConfigSoulShards config) {
        Channels.CONFIG_UPDATE.sendToServer(new ConfigUpdate(config));
    }
}
