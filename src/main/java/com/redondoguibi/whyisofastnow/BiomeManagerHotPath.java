package com.redondoguibi.whyisofastnow;

/**
 * Deferred BiomeManager candidate.
 *
 * <p>Biome lookup feeds world generation and gameplay; changing coordinate transforms, quart
 * boundaries or tie-breaking without profile evidence and exact parity risks different worlds.</p>
 */
public final class BiomeManagerHotPath extends DeferredServerHotPath {
    public BiomeManagerHotPath() {
        super("biome-manager", Config.BIOME_MANAGER_HOT_PATH);
    }
}
