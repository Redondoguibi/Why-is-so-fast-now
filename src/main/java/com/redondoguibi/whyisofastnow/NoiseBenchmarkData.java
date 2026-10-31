package com.redondoguibi.whyisofastnow;

import java.util.Random;

final class NoiseBenchmarkData {
    static final long SEED = 0xC0FFEEL;
    static final byte[] PERMUTATION = createPermutation(SEED);

    private NoiseBenchmarkData() {
    }

    static void fillCoordinates(long seed, double[] xs, double[] ys, double[] zs) {
        if (xs.length != ys.length || xs.length != zs.length) {
            throw new IllegalArgumentException("Noise coordinate arrays must have equal lengths.");
        }
        Random random = new Random(seed);
        for (int index = 0; index < xs.length; index++) {
            xs[index] = random.nextDouble() * 2_000.0 - 1_000.0;
            ys[index] = random.nextDouble() * 2_000.0 - 1_000.0;
            zs[index] = random.nextDouble() * 2_000.0 - 1_000.0;
        }
    }

    private static byte[] createPermutation(long seed) {
        byte[] permutation = new byte[256];
        for (int index = 0; index < permutation.length; index++) {
            permutation[index] = (byte) index;
        }
        Random random = new Random(seed);
        for (int index = permutation.length - 1; index > 0; index--) {
            int swapIndex = random.nextInt(index + 1);
            byte value = permutation[index];
            permutation[index] = permutation[swapIndex];
            permutation[swapIndex] = value;
        }
        return permutation;
    }
}
