package com.redondoguibi.whyisofastnow;

/**
 * A reusable Perlin-noise implementation with both scalar and batch entry points.
 *
 * <p>The batch method keeps test and benchmark inputs independent of Minecraft so kernel
 * behavior can be measured without starting a game.</p>
 */
public interface NoiseKernel {
    /** Returns the stable identifier used in diagnostics and benchmark output. */
    default String id() {
        return getClass().getSimpleName();
    }

    /** Returns whether the specialized implementation is available in this JVM. */
    default boolean isAvailable() {
        return true;
    }

    /**
     * Samples matching coordinates into {@code out}; all arrays must have equal lengths.
     *
     * <p>A fixed permutation makes repeated benchmark runs deterministic.</p>
     */
    default void sample(double[] xs, double[] ys, double[] zs, double[] out) {
        if (xs.length != ys.length || xs.length != zs.length || xs.length != out.length) {
            throw new IllegalArgumentException("Noise coordinate and output arrays must have equal lengths.");
        }
        byte[] permutation = NoiseBenchmarkData.PERMUTATION;
        for (int index = 0; index < out.length; index++) {
            out[index] = noise(permutation, 0.0, 0.0, 0.0, xs[index], ys[index], zs[index], 0.0, 0.0);
        }
    }

    double sample(byte[] permutation, int gridX, int gridY, int gridZ, double deltaX, double weirdDeltaY, double deltaZ, double deltaY);

    default double noise(byte[] permutation, double xo, double yo, double zo, double x, double y, double z, double yScale, double yMax) {
        double d0 = x + xo;
        double d1 = y + yo;
        double d2 = z + zo;
        int i = NoiseMath.floor(d0);
        int j = NoiseMath.floor(d1);
        int k = NoiseMath.floor(d2);
        double d3 = d0 - (double) i;
        double d4 = d1 - (double) j;
        double d5 = d2 - (double) k;
        double d6;
        if (yScale != 0.0) {
            double d7;
            if (yMax >= 0.0 && yMax < d4) {
                d7 = yMax;
            } else {
                d7 = d4;
            }

            d6 = (double) NoiseMath.floor(d7 / yScale + 1.0E-7F) * yScale;
        } else {
            d6 = 0.0;
        }

        return sample(permutation, i, j, k, d3, d4 - d6, d5, d4);
    }
}
