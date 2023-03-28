package info.x2a.soulshards.api;

public interface IShardTier {

    int getKillRequirement();

    boolean checkPlayer();

    boolean checkLight();

    boolean checkRedstone();

    int getSpawnAmount();

    int getCooldown();

    int getIndex();
}
