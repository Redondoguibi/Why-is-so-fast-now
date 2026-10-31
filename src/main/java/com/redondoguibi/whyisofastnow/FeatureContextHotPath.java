package com.redondoguibi.whyisofastnow;

/**
 * Deferred feature-placement context and allocation candidate.
 *
 * <p>Pooling a placement context can leak mutable random, origin or level state between features;
 * it must remain untouched until ownership and random-stream parity are established.</p>
 */
public final class FeatureContextHotPath extends DeferredServerHotPath {
    public FeatureContextHotPath() {
        super("feature-context", Config.FEATURE_CONTEXT_HOT_PATH);
    }
}
