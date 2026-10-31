package com.redondoguibi.whyisofastnow;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * Isolated JMH comparison of scalar and vector noise kernels without Minecraft dependencies.
 *
 * <p>Each thread owns its input buffers so the measured calls do not contend on benchmark state.</p>
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class NoiseKernelBench {
    private static final int BATCH = 256;
    private final double[] xs = new double[BATCH];
    private final double[] ys = new double[BATCH];
    private final double[] zs = new double[BATCH];
    private final double[] output = new double[BATCH];
    private final NoiseKernel scalar = ScalarNoiseKernel.INSTANCE;
    private final NoiseKernel vector = VectorNoiseKernel.INSTANCE;

    @Setup
    public void setup() {
        NoiseBenchmarkData.fillCoordinates(NoiseBenchmarkData.SEED, xs, ys, zs);
    }

    @Benchmark
    public double scalarNoiseKernel() {
        scalar.sample(xs, ys, zs, output);
        return checksum();
    }

    @Benchmark
    public double vectorNoiseKernel() {
        vector.sample(xs, ys, zs, output);
        return checksum();
    }

    private double checksum() {
        double sum = 0.0;
        for (double value : output) {
            sum += value;
        }
        return sum;
    }
}
