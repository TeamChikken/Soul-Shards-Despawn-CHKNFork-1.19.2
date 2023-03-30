package info.x2a.soulshards.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import info.x2a.soulshards.SoulShards;
import info.x2a.soulshards.compat.clothconfig.SoulShardsConfigScreen;

public class SoulShardsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (SoulShards.IS_CLOTH_CONFIG_LOADED) {
                return new SoulShardsConfigScreen(parent).screen();
            } else {
                return null;
            }
        };
    }
}
