package com.redondoguibi.whyisofastnow;

import java.lang.reflect.InvocationTargetException;

/**
 * Optional Vector API adapter that remains loadable on JVMs without the incubator module.
 *
 * <p>Keeping Vector API references in a separately loaded implementation prevents a missing
 * module from turning an optional optimization into a mod startup failure.</p>
 */
public final class VectorNoiseKernel implements NoiseKernel {
    public static final VectorNoiseKernel INSTANCE = new VectorNoiseKernel();

    private final NoiseKernel delegate;
    private final boolean vectorAvailable;
    private final Throwable loadFailure;

    public VectorNoiseKernel() {
        LoadResult result = loadVectorImplementation();
        NoiseKernel loaded = result.kernel();
        vectorAvailable = loaded != null;
        delegate = vectorAvailable ? loaded : ScalarNoiseKernel.INSTANCE;
        loadFailure = result.failure();
    }

    @Override
    public String id() {
        return "vector";
    }

    @Override
    public boolean isAvailable() {
        return vectorAvailable;
    }

    Throwable loadFailure() {
        return loadFailure;
    }

    @Override
    public void sample(double[] xs, double[] ys, double[] zs, double[] out) {
        delegate.sample(xs, ys, zs, out);
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
        return delegate.sample(permutation, gridX, gridY, gridZ, deltaX, weirdDeltaY, deltaZ, deltaY);
    }

    private static LoadResult loadVectorImplementation() {
        if (ModuleLayer.boot().findModule("jdk.incubator.vector").isEmpty()) {
            return new LoadResult(null, null);
        }
        try {
            Class<?> implementation = Class.forName(
                    "com.redondoguibi.whyisofastnow.IncubatorVectorNoiseKernel",
                    true,
                    VectorNoiseKernel.class.getClassLoader());
            return new LoadResult((NoiseKernel) implementation.getDeclaredConstructor().newInstance(), null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
                 | InstantiationException | InvocationTargetException | LinkageError | SecurityException exception) {
            return new LoadResult(null, exception);
        }
    }

    private record LoadResult(NoiseKernel kernel, Throwable failure) {
    }
}
