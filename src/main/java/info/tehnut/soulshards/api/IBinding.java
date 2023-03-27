package info.tehnut.soulshards.api;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public interface IBinding {

    UUID getOwner();

    ResourceLocation getBoundEntity();

    int getKills();

    IBinding addKills(int amount);

    IShardTier getTier();
}
