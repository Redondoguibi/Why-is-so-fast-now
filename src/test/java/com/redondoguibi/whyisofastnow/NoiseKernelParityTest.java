package com.redondoguibi.whyisofastnow;

import java.lang.reflect.Field;
import java.util.Random;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoiseKernelParityTest {
    private static final long[] SEEDS = {0L, 1L, -1L, 0x5EEDC0DEL};
    private static final double[] Y_SCALES = {0.0, 0.125, 0.5, 1.0, 2.0};
    private static final double[] Y_MAXES = {-1.0, 0.0, 0.25, 0.5, 1.0};

    @Test
    void scalarKernelMatchesVanillaBitForBit() throws ReflectiveOperationException {
        assertMatchesVanilla(ScalarNoiseKernel.INSTANCE);
    }

    @Test
    void vectorKernelAvailabilityMatchesBootModule() {
        boolean moduleAvailable = ModuleLayer.boot().findModule("jdk.incubator.vector").isPresent();
        assertEquals(moduleAvailable, NoiseKernels.vectorKernel() != null);
    }

    @Test
    void vectorKernelMatchesVanillaBitForBitWhenModuleIsAvailable() throws ReflectiveOperationException {
        NoiseKernel vectorKernel = NoiseKernels.vectorKernel();
        Assumptions.assumeTrue(vectorKernel != null, "jdk.incubator.vector is not available in this runtime");
        assertMatchesVanilla(vectorKernel);
    }

    @SuppressWarnings("deprecation")
    private static void assertMatchesVanilla(NoiseKernel kernel) throws ReflectiveOperationException {
        Field permutationField = ImprovedNoise.class.getDeclaredField("p");
        permutationField.setAccessible(true);

        Random coordinates = new Random(0x71A11L);
        for (long seed : SEEDS) {
            ImprovedNoise vanilla = new ImprovedNoise(RandomSource.create(seed));
            byte[] permutation = (byte[]) permutationField.get(vanilla);

            for (int sample = 0; sample < 20_000; sample++) {
                double x = coordinates.nextDouble() * 20_000.0 - 10_000.0;
                double y = coordinates.nextDouble() * 2_000.0 - 1_000.0;
                double z = coordinates.nextDouble() * 20_000.0 - 10_000.0;
                double yScale = Y_SCALES[sample % Y_SCALES.length];
                double yMax = Y_MAXES[(sample / Y_SCALES.length) % Y_MAXES.length];

                double expected = vanilla.noise(x, y, z, yScale, yMax);
                double actual = kernel.noise(permutation, vanilla.xo, vanilla.yo, vanilla.zo, x, y, z, yScale, yMax);

                assertEquals(
                        Double.doubleToRawLongBits(expected),
                        Double.doubleToRawLongBits(actual),
                        "seed=" + seed + ", sample=" + sample + ", yScale=" + yScale + ", yMax=" + yMax);
            }
        }
    }
}
