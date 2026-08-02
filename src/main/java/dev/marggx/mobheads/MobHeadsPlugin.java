package dev.marggx.mobheads;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import dev.marggx.mobheads.commands.MobHeadsCommand;
import dev.marggx.mobheads.systems.MobHeadDropSystem;

import javax.annotation.Nonnull;

public class MobHeadsPlugin extends JavaPlugin {
    private static MobHeadsPlugin INSTANCE;
    private final Config<MobHeadConfig> config = withConfig("MobHeadConfig", MobHeadConfig.CODEC);

    public static MobHeadsPlugin get() {
        return INSTANCE;
    }

    public MobHeadsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
    }

    public Config<MobHeadConfig> getConfig() {
        return config;
    }

    @Override
    protected void setup() {
        config.save();
        this.getEntityStoreRegistry().registerSystem(new MobHeadDropSystem());
        this.getCommandRegistry().registerCommand(new MobHeadsCommand());
    }

    @Override
    protected void start() {}
}
