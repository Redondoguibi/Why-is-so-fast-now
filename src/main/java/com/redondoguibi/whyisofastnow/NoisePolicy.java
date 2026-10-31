package com.redondoguibi.whyisofastnow;

/**
 * Holds the kernel selected by the config. Resolved once on config load/reload so the
 * noise hot path only reads a volatile field. {@code null} means: use vanilla.
 * Before the config is loaded the policy is null, so vanilla behavior is used.
 */
public final class NoisePolicy {
    private static volatile NoiseKernel active;

    private NoisePolicy() {
    }

    public static NoiseKernel active() {
        return active;
    }

    static void refresh() {
        NoiseKernel kernel = null;
        if (Config.VECTOR_NOISE.get()) {
            kernel = NoiseKernels.vectorKernel();
        }
        if (kernel == null && Config.SCALAR_NOISE.get()) {
            kernel = ScalarNoiseKernel.INSTANCE;
        }
        active = kernel;
        Whyissofastnow.LOGGER.info("Noise kernel: {}", kernel == null ? "vanilla" : kernel.getClass().getSimpleName());
    }
}
