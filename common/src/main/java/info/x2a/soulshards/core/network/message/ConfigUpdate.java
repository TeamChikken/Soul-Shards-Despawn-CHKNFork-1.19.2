package info.x2a.soulshards.core.network.message;

import com.google.gson.reflect.TypeToken;
import dev.architectury.networking.NetworkManager;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.core.ConfigSoulShards;
import info.x2a.soulshards.core.network.Channels;
import info.x2a.soulshards.core.util.JsonUtil;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class ConfigUpdate {
    public final ConfigSoulShards config;

    public ConfigUpdate(ConfigSoulShards config) {
        this.config = config;
    }

    public ConfigUpdate(FriendlyByteBuf buf) {
        this.config = JsonUtil.fromJson(TypeToken.get(ConfigSoulShards.class), buf.toString(StandardCharsets.UTF_8));
    }

    public void encode(FriendlyByteBuf buf) {
        var tkn = TypeToken.get(ConfigSoulShards.class);
        var json = JsonUtil.getJson(config, tkn);
        buf.writeBytes(json.getBytes(StandardCharsets.UTF_8));
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        var player = ctx.get().getPlayer();
        if (!player.isLocalPlayer() && !player.getServer()
                                              .getPlayerList()
                                              .isOp(ctx.get().getPlayer().getGameProfile())) {
            return;
        } else {
            Channels.CONFIG_UPDATE.sendToPlayers(player.getServer()
                                                       .getPlayerList()
                                                       .getPlayers()
                                                       .stream()
                                                       .filter(p -> !p.getUUID().equals(player.getUUID()))
                                                       .toList(), new ConfigUpdate(config));
        }
        SoulShards.CONFIG = config;
    }
}
