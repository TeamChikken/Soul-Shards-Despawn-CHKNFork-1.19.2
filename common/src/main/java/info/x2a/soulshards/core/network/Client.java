package info.x2a.soulshards.core.network;

import info.x2a.soulshards.core.config.ConfigServer;
import info.x2a.soulshards.core.network.message.ConfigUpdate;

public class Client {
    public static void sendConfig(ConfigServer config) {
        Channels.CONFIG_UPDATE.sendToServer(new ConfigUpdate(config));
    }
}
