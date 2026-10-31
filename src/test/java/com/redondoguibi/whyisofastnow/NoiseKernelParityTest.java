package com.redondoguibi.whyisofastnow;

import java.lang.reflect.Field;
import java.util.Random;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoiseKernelParityTest {
    private static final long SEED = 0xC0FFEEL;
    private static final int SAMPLE_COUNT = 10_000;

    @Test
    void scalarAndVectorBatchKernelsMatchBitForBitForTenThousandSamples() {
        double[] xs = new double[SAMPLE_COUNT];
        double[] ys = new double[SAMPLE_COUNT];
        double[] zs = new double[SAMPLE_COUNT];
        double[] scalarOutput = new double[SAMPLE_COUNT];
        double[] vectorOutput = new double[SAMPLE_COUNT];
        NoiseBenchmarkData.fillCoordinates(SEED, xs, ys, zs);

        ScalarNoiseKernel.INSTANCE.sample(xs, ys, zs, scalarOutput);
        VectorNoiseKernel.INSTANCE.sample(xs, ys, zs, vectorOutput);

        for (int index = 0; index < SAMPLE_COUNT; index++) {
            assertEquals(
                    Double.doubleToRawLongBits(scalarOutput[index]),
                    Double.doubleToRawLongBits(vectorOutput[index]),
                    "sample=" + index);
        }
    }

    @Test
    void scalarKernelMatchesVanillaBitForBit() throws ReflectiveOperationException {
        assertMatchesVanilla(ScalarNoiseKernel.INSTANCE);
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

        ImprovedNoise vanilla = new ImprovedNoise(RandomSource.create(SEED));
        byte[] permutation = (byte[]) permutationField.get(vanilla);
        Random coordinates = new Random(SEED);
        for (int sample = 0; sample < SAMPLE_COUNT; sample++) {
            double x = coordinates.nextDouble() * 20_000.0 - 10_000.0;
            double y = coordinates.nextDouble() * 2_000.0 - 1_000.0;
            double z = coordinates.nextDouble() * 20_000.0 - 10_000.0;
            double yScale = sample % 5 == 0 ? 0.0 : 0.125;
            double yMax = sample % 7 == 0 ? 0.25 : -1.0;

            double expected = vanilla.noise(x, y, z, yScale, yMax);
            double actual = kernel.noise(permutation, vanilla.xo, vanilla.yo, vanilla.zo, x, y, z, yScale, yMax);

            assertEquals(
                    Double.doubleToRawLongBits(expected),
                    Double.doubleToRawLongBits(actual),
                    "sample=" + sample);
        }
    }
}
