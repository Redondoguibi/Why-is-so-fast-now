package com.redondoguibi.whyisofastnow;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;

/**
 * SIMD implementation loaded reflectively only when Java's incubator Vector API is resolved.
 *
 * <p>The per-corner dot products are independent, while their scalar inputs and interpolation
 * order stay unchanged to preserve vanilla's exact result.</p>
 */
public final class IncubatorVectorNoiseKernel implements NoiseKernel {
    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;
    private static final int[][] GRADIENT = {
            {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
            {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1},
            {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}
    };
    private static final ThreadLocal<Scratch> SCRATCH = ThreadLocal.withInitial(Scratch::new);

    public IncubatorVectorNoiseKernel() {
    }

    @Override
    public String id() {
        return "vector";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public double sample(
            byte[] permutation,
            int gridX,
            int gridY,
            int gridZ,
            double deltaX,
            double weirdDeltaY,
            double deltaZ,
            double deltaY) {
        int i = permutation[gridX & 0xFF] & 0xFF;
        int j = permutation[(gridX + 1) & 0xFF] & 0xFF;
        int k = permutation[(i + gridY) & 0xFF] & 0xFF;
        int l = permutation[(i + gridY + 1) & 0xFF] & 0xFF;
        int i1 = permutation[(j + gridY) & 0xFF] & 0xFF;
        int j1 = permutation[(j + gridY + 1) & 0xFF] & 0xFF;

        Scratch scratch = SCRATCH.get();
        setCorner(scratch, 0, permutation[(k + gridZ) & 0xFF] & 0xFF, deltaX, weirdDeltaY, deltaZ);
        setCorner(scratch, 1, permutation[(i1 + gridZ) & 0xFF] & 0xFF, deltaX - 1.0, weirdDeltaY, deltaZ);
        setCorner(scratch, 2, permutation[(l + gridZ) & 0xFF] & 0xFF, deltaX, weirdDeltaY - 1.0, deltaZ);
        setCorner(scratch, 3, permutation[(j1 + gridZ) & 0xFF] & 0xFF, deltaX - 1.0, weirdDeltaY - 1.0, deltaZ);
        setCorner(scratch, 4, permutation[(k + gridZ + 1) & 0xFF] & 0xFF, deltaX, weirdDeltaY, deltaZ - 1.0);
        setCorner(scratch, 5, permutation[(i1 + gridZ + 1) & 0xFF] & 0xFF, deltaX - 1.0, weirdDeltaY, deltaZ - 1.0);
        setCorner(scratch, 6, permutation[(l + gridZ + 1) & 0xFF] & 0xFF, deltaX, weirdDeltaY - 1.0, deltaZ - 1.0);
        setCorner(scratch, 7, permutation[(j1 + gridZ + 1) & 0xFF] & 0xFF, deltaX - 1.0, weirdDeltaY - 1.0, deltaZ - 1.0);
        calculateDots(scratch);

        return NoiseMath.lerp3(
                NoiseMath.smoothstep(deltaX),
                NoiseMath.smoothstep(deltaY),
                NoiseMath.smoothstep(deltaZ),
                scratch.dots[0],
                scratch.dots[1],
                scratch.dots[2],
                scratch.dots[3],
                scratch.dots[4],
                scratch.dots[5],
                scratch.dots[6],
                scratch.dots[7]);
    }

    private static void setCorner(Scratch scratch, int corner, int gradientIndex, double x, double y, double z) {
        int[] gradient = GRADIENT[gradientIndex & 15];
        scratch.gradientX[corner] = gradient[0];
        scratch.gradientY[corner] = gradient[1];
        scratch.gradientZ[corner] = gradient[2];
        scratch.offsetX[corner] = x;
        scratch.offsetY[corner] = y;
        scratch.offsetZ[corner] = z;
    }

    private static void calculateDots(Scratch scratch) {
        int lanes = SPECIES.length();
        for (int offset = 0; offset < 8; offset += lanes) {
            DoubleVector x = DoubleVector.fromArray(SPECIES, scratch.offsetX, offset);
            DoubleVector y = DoubleVector.fromArray(SPECIES, scratch.offsetY, offset);
            DoubleVector z = DoubleVector.fromArray(SPECIES, scratch.offsetZ, offset);
            DoubleVector gx = DoubleVector.fromArray(SPECIES, scratch.gradientX, offset);
            DoubleVector gy = DoubleVector.fromArray(SPECIES, scratch.gradientY, offset);
            DoubleVector gz = DoubleVector.fromArray(SPECIES, scratch.gradientZ, offset);
            gx.mul(x).add(gy.mul(y)).add(gz.mul(z)).intoArray(scratch.dots, offset);
        }
    }

    private static final class Scratch {
        private final double[] offsetX = new double[8];
        private final double[] offsetY = new double[8];
        private final double[] offsetZ = new double[8];
        private final double[] gradientX = new double[8];
        private final double[] gradientY = new double[8];
        private final double[] gradientZ = new double[8];
        private final double[] dots = new double[8];
    }
}
