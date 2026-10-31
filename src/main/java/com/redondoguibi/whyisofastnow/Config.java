package com.redondoguibi.whyisofastnow;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SCALAR_NOISE = BUILDER
            .comment("Use the allocation-free scalar noise kernel. Leave disabled until profiling confirms a benefit.")
            .define("scalarNoise", false);

    public static final ModConfigSpec.BooleanValue VECTOR_NOISE = BUILDER
            .comment("Use the optional Java Vector API noise kernel when the module is enabled.")
            .define("vectorNoise", false);

    public static final ModConfigSpec.BooleanValue NATURAL_SPAWNER_HOT_PATH = BUILDER
            .comment("Reserved for a Spark-justified NaturalSpawner optimization; requires recorded profile evidence.")
            .define("naturalSpawnerHotPath", false);

    public static final ModConfigSpec.BooleanValue BIOME_MANAGER_HOT_PATH = BUILDER
            .comment("Reserved for a Spark-justified BiomeManager optimization; requires recorded profile evidence.")
            .define("biomeManagerHotPath", false);

    public static final ModConfigSpec.BooleanValue FEATURE_CONTEXT_HOT_PATH = BUILDER
            .comment("Reserved for a Spark-justified feature placement context optimization; requires recorded profile evidence.")
            .define("featureContextHotPath", false);

    public static final ModConfigSpec.BooleanValue BLOCKABLE_EVENT_LOOP_HOT_PATH = BUILDER
            .comment("Reserved for a Spark-justified server event loop optimization; requires recorded profile evidence.")
            .define("blockableEventLoopHotPath", false);

    public static final ModConfigSpec.BooleanValue ADAPTIVE_RENDER_DISTANCE = BUILDER
            .comment("Opt in to gradually lowering and restoring render distance based on sampled FPS.")
            .define("adaptiveRenderDistance", false);

    public static final ModConfigSpec.IntValue TARGET_FPS = BUILDER
            .comment("FPS threshold below which render distance may be reduced.")
            .defineInRange("targetFps", 45, 30, 240);

    public static final ModConfigSpec.IntValue RECOVERY_FPS_MARGIN = BUILDER
            .comment("FPS above the target required before render distance is restored.")
            .defineInRange("recoveryFpsMargin", 13, 5, 120);

    public static final ModConfigSpec.IntValue MIN_RENDER_DISTANCE = BUILDER
            .comment("Lowest render distance the adaptive controller may use.")
            .defineInRange("minRenderDistance", 6, 2, 32);

    public static final ModConfigSpec.IntValue DOWN_SAMPLES = BUILDER
            .comment("Low-FPS samples required before reducing render distance.")
            .defineInRange("downSamples", 3, 1, 30);

    public static final ModConfigSpec.IntValue UP_SAMPLES = BUILDER
            .comment("High-FPS samples required before restoring render distance.")
            .defineInRange("upSamples", 10, 1, 60);

    public static final ModConfigSpec.IntValue COOLDOWN_MS = BUILDER
            .comment("Minimum time between adaptive render-distance changes.")
            .defineInRange("cooldownMs", 5000, 1000, 60000);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
