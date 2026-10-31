package com.redondoguibi.whyisofastnow;

/**
 * Gradient table of vanilla ImprovedNoise as flat primitive arrays.
 * Values are kept as doubles so that {@code gx * x} is the same IEEE operation
 * as vanilla's {@code (double) int * x}; this preserves bit-exact output.
 */
final class NoiseGradients {
    static final double[] X = {1, -1, 1, -1, 1, -1, 1, -1, 0, 0, 0, 0, 1, 0, -1, 0};
    static final double[] Y = {1, 1, -1, -1, 0, 0, 0, 0, 1, -1, 1, -1, 1, -1, 1, -1};
    static final double[] Z = {0, 0, 0, 0, 1, 1, -1, -1, 1, 1, -1, -1, 0, 1, 0, -1};

    private NoiseGradients() {
    }
}
