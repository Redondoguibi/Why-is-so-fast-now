package com.redondoguibi.whyisofastnow;

/**
 * The first registered hot path, selecting scalar or optional vector noise from config.
 *
 * <p>Noise remains vanilla unless an explicit config toggle enables this hook.</p>
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
}
