package com.redondoguibi.whyisofastnow;

/**
 * Deferred NaturalSpawner candidate.
 *
 * <p>Spawn caps, spawn categories, local difficulty and random consumption must be profiled and
 * preserved before a replacement can safely run.</p>
 */
public final class NaturalSpawnerHotPath extends DeferredServerHotPath {
    public NaturalSpawnerHotPath() {
        super("natural-spawner", Config.NATURAL_SPAWNER_HOT_PATH);
    }
}
