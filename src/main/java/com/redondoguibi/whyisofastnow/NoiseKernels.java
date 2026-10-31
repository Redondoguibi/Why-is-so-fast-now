package com.redondoguibi.whyisofastnow;

import java.lang.reflect.InvocationTargetException;

public final class NoiseKernels {
    private static final boolean VECTOR_MODULE_PRESENT = ModuleLayer.boot().findModule("jdk.incubator.vector").isPresent();

    private NoiseKernels() {
    }

    public static NoiseKernel vectorKernel() {
        if (!VECTOR_MODULE_PRESENT) {
            return null;
        }
        return VectorHolder.INSTANCE;
    }

    private static NoiseKernel loadVectorKernel() {
        try {
            Class<?> kernelClass = Class.forName(
                    "com.redondoguibi.whyisofastnow.VectorNoiseKernel",
                    true,
                    NoiseKernels.class.getClassLoader());
            return (NoiseKernel) kernelClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
                 | InstantiationException | InvocationTargetException | LinkageError | SecurityException exception) {
            Whyissofastnow.LOGGER.warn("Vector API noise is unavailable; using the selected Java fallback.", exception);
            return null;
        }
    }

    private static final class VectorHolder {
        private static final NoiseKernel INSTANCE = loadVectorKernel();
    }
}
