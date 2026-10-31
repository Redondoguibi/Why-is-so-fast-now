package com.redondoguibi.whyisofastnow;

import net.minecraft.util.Mth;

public final class ScalarNoiseKernel implements NoiseKernel {
    public static final ScalarNoiseKernel INSTANCE = new ScalarNoiseKernel();

    private ScalarNoiseKernel() {
    }

    @Override
    public double sample(byte[] permutation, int gridX, int gridY, int gridZ, double deltaX, double weirdDeltaY, double deltaZ, double deltaY) {
        int i = permutation[gridX & 0xFF] & 0xFF;
        int j = permutation[(gridX + 1) & 0xFF] & 0xFF;
        int k = permutation[(i + gridY) & 0xFF] & 0xFF;
        int l = permutation[(i + gridY + 1) & 0xFF] & 0xFF;
        int i1 = permutation[(j + gridY) & 0xFF] & 0xFF;
        int j1 = permutation[(j + gridY + 1) & 0xFF] & 0xFF;

        double d0 = gradDot(permutation[(k + gridZ) & 0xFF] & 0xFF, deltaX, weirdDeltaY, deltaZ);
        double d1 = gradDot(permutation[(i1 + gridZ) & 0xFF] & 0xFF, deltaX - 1.0, weirdDeltaY, deltaZ);
        double d2 = gradDot(permutation[(l + gridZ) & 0xFF] & 0xFF, deltaX, weirdDeltaY - 1.0, deltaZ);
        double d3 = gradDot(permutation[(j1 + gridZ) & 0xFF] & 0xFF, deltaX - 1.0, weirdDeltaY - 1.0, deltaZ);
        double d4 = gradDot(permutation[(k + gridZ + 1) & 0xFF] & 0xFF, deltaX, weirdDeltaY, deltaZ - 1.0);
        double d5 = gradDot(permutation[(i1 + gridZ + 1) & 0xFF] & 0xFF, deltaX - 1.0, weirdDeltaY, deltaZ - 1.0);
        double d6 = gradDot(permutation[(l + gridZ + 1) & 0xFF] & 0xFF, deltaX, weirdDeltaY - 1.0, deltaZ - 1.0);
        double d7 = gradDot(permutation[(j1 + gridZ + 1) & 0xFF] & 0xFF, deltaX - 1.0, weirdDeltaY - 1.0, deltaZ - 1.0);
        double d8 = Mth.smoothstep(deltaX);
        double d9 = Mth.smoothstep(deltaY);
        double d10 = Mth.smoothstep(deltaZ);
        return Mth.lerp3(d8, d9, d10, d0, d1, d2, d3, d4, d5, d6, d7);
    }

    private static double gradDot(int gradIndex, double xFactor, double yFactor, double zFactor) {
        int g = gradIndex & 15;
        return NoiseGradients.X[g] * xFactor + NoiseGradients.Y[g] * yFactor + NoiseGradients.Z[g] * zFactor;
    }
}
