package com.redondoguibi.whyisofastnow;

import net.minecraft.util.Mth;

public interface NoiseKernel {
    double sample(byte[] permutation, int gridX, int gridY, int gridZ, double deltaX, double weirdDeltaY, double deltaZ, double deltaY);

    default double noise(byte[] permutation, double xo, double yo, double zo, double x, double y, double z, double yScale, double yMax) {
        double d0 = x + xo;
        double d1 = y + yo;
        double d2 = z + zo;
        int i = Mth.floor(d0);
        int j = Mth.floor(d1);
        int k = Mth.floor(d2);
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

            d6 = (double) Mth.floor(d7 / yScale + 1.0E-7F) * yScale;
        } else {
            d6 = 0.0;
        }

        return sample(permutation, i, j, k, d3, d4 - d6, d5, d4);
    }
}
