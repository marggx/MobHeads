package dev.marggx.mobheads;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

public class MobHeadConfig {
    public static final BuilderCodec<MobHeadConfig> CODEC = BuilderCodec.builder(MobHeadConfig.class, MobHeadConfig::new)
        .append(
            new KeyedCodec<>("DropChance", Codec.DOUBLE),
            (config, value) -> config.dropChance = value,
            config -> config.dropChance
        )
            .add()
        .append(
            new KeyedCodec<>("ExcludedHeads", new ArrayCodec<>(Codec.STRING, String[]::new)),
            (config, value) -> config.excludedHeads = value,
            config -> config.excludedHeads
        )
            .add()
        .build();

    private double dropChance = 2.5;
    private String[] excludedHeads = new String[0];

    public MobHeadConfig() {
    }

    public double getDropChance() {
        return dropChance;
    }

    public void setDropChance(double dropChance) {
        this.dropChance = dropChance;
    }

    public String[] getExcludedHeads() {
        return excludedHeads;
    }

    public void setExcludedHeads(String[] excludedHeads) {
        this.excludedHeads = excludedHeads;
    }
}
