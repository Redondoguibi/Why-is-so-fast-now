package com.redondoguibi.whyisofastnow;

import java.util.Optional;

/**
 * The first registered hot path, selecting scalar or optional vector noise from config.
 *
 * <p>Noise remains vanilla unless an explicit config toggle enables this hook. No Spark profile
 * has yet justified this existing optimization, so it deliberately reports no evidence.</p>
 */
public final class NoiseKernelHotPath implements HotPath {
    @Override
    public String id() {
        return "noise-kernel";
    }

    @Override
    public boolean shouldEnable() {
        return Config.SCALAR_NOISE.get() || Config.VECTOR_NOISE.get();
    }

    @Override
    public void apply() {
        NoiseKernels.refreshFromConfig();
    }

    @Override
    public Optional<HotPathEvidence> evidence() {
        return Optional.empty();
    }
}
