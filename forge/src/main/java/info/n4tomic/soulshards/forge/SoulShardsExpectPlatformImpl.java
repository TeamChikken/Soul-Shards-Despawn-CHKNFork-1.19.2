package info.n4tomic.soulshards.forge;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class SoulShardsExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
