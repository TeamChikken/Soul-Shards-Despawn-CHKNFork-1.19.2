package info.x2a.soulshards.core.network;


import dev.architectury.networking.NetworkChannel;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.core.network.message.ConfigUpdate;

public class Channels {
    public static final NetworkChannel CONFIG_UPDATE = NetworkChannel.create(Packets.CONFIG_UPDATE);

    public static void init() {
        CONFIG_UPDATE.register(ConfigUpdate.class, ConfigUpdate::encode, ConfigUpdate::new, ConfigUpdate::apply);
        SoulShards.Log.info("Networking initialised");
    }
}
