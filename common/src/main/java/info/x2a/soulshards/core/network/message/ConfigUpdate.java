package info.x2a.soulshards.core.network.message;

import com.google.gson.reflect.TypeToken;
import dev.architectury.networking.NetworkManager;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.core.config.ConfigServer;
import info.x2a.soulshards.core.network.Channels;
import info.x2a.soulshards.core.util.JsonUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public final class ConfigUpdate {
    private final ConfigServer.ConfigBalance balance;
    private final ConfigServer.ConfigEntityList entityList;


    public ConfigUpdate(ConfigServer.ConfigBalance balance, ConfigServer.ConfigEntityList entityList) {
        this.balance = balance;
        this.entityList = entityList;
    }

    public ConfigUpdate(ConfigServer config) {
        this.balance = config.getBalance();
        this.entityList = config.entityList;
    }

    public ConfigUpdate(FriendlyByteBuf buf) {
        this.balance = JsonUtil.fromJson(TypeToken.get(ConfigServer.ConfigBalance.class), new String(buf.readByteArray(), StandardCharsets.UTF_8));
        this.entityList = JsonUtil.fromJson(TypeToken.get(ConfigServer.ConfigEntityList.class), new String(buf.readByteArray(), StandardCharsets.UTF_8));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeByteArray(JsonUtil.getJson(balance, TypeToken.get(ConfigServer.ConfigBalance.class))
                                   .getBytes(StandardCharsets.UTF_8));
        buf.writeByteArray(JsonUtil.getJson(entityList, TypeToken.get(ConfigServer.ConfigEntityList.class))
                                   .getBytes(StandardCharsets.UTF_8));
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        try {
            var player = ctx.get().getPlayer();
            if (player instanceof ServerPlayer) { // we are serverside
                var server = player.getServer();
                if (server.getPlayerList().isOp(player.getGameProfile())) {
                    Channels.CONFIG_UPDATE.sendToPlayers(server
                            .getPlayerList()
                            .getPlayers()
                            .stream()
                            .filter(p -> !p.getUUID().equals(player.getUUID()))
                            .toList(), new ConfigUpdate(balance, entityList));
                    SoulShards.CONFIG_SERVER.balance = balance;
                    SoulShards.CONFIG_SERVER.entityList = entityList;
                    SoulShards.saveServer();
                }
            } else { // we are clientside
                SoulShards.CONFIG_SERVER.balance = balance;
                SoulShards.CONFIG_SERVER.entityList = entityList;
            }
        } catch (Exception e) {
            SoulShards.Log.error("recv config: {}", e.getMessage());
        }
    }
}
