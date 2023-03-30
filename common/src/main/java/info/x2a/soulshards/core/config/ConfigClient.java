package info.x2a.soulshards.core.config;

public class ConfigClient {
    public boolean displayDurabilityBar;

    public ConfigClient(boolean displayDurabilityBar) {
        this.displayDurabilityBar = displayDurabilityBar;
    }

    public ConfigClient() {
        this(true);
    }

    public boolean displayDurabilityBar() {
        return displayDurabilityBar;
    }
}
