package com.redondoguibi.whyisofastnow;

/**
 * Selects configured kernels while preserving vanilla behavior when all optimizations are off.
 *
 * <p>Active selection is refreshed on config load/reload, rather than doing config lookups inside
 * the hot noise call.</p>
 */
public final class NoiseKernels {
    private static volatile NoiseKernel activeKernel;

    private NoiseKernels() {
    }

    /** Returns the vector kernel only when its module-backed implementation is available. */
    public static NoiseKernel vectorKernel() {
        return VectorNoiseKernel.INSTANCE.isAvailable() ? VectorNoiseKernel.INSTANCE : null;
    }

    /** Returns the currently selected kernel, or {@code null} to retain the vanilla path. */
    public static NoiseKernel activeKernel() {
        return activeKernel;
    }

    static void refreshFromConfig() {
        activeKernel = null;
        if (Config.VECTOR_NOISE.get()) {
            activeKernel = VectorNoiseKernel.INSTANCE;
        } else if (Config.SCALAR_NOISE.get()) {
            activeKernel = ScalarNoiseKernel.INSTANCE;
        }
    }

    static void clearActiveKernel() {
        activeKernel = null;
    }
}
