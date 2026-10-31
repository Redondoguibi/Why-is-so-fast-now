package com.redondoguibi.whyisofastnow;

import java.util.Locale;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Registers user-invoked diagnostics for kernel availability and deterministic timing.
 *
 * <p>The benchmark is explicit rather than automatic because timing adds temporary CPU work.</p>
 */
@EventBusSubscriber(modid = Whyissofastnow.MODID)
public final class WhyFastCommands {
    private static final int WARMUP_ITERATIONS = 200;
    private static final int MEASUREMENT_ITERATIONS = 2_000;
    private static final int BATCH = 256;
    private static volatile double benchmarkSink;

    private WhyFastCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("whyfast")
                .then(Commands.literal("status").executes(context -> showStatus(context.getSource())))
                .then(Commands.literal("benchmark").executes(context -> runBenchmark(context.getSource()))));
    }

    private static int showStatus(CommandSourceStack source) {
        boolean vectorModule = ModuleLayer.boot().findModule("jdk.incubator.vector").isPresent();
        String vectorStatus;
        if (!vectorModule) {
            vectorStatus = "unavailable; scalar fallback; use --add-modules=jdk.incubator.vector";
        } else if (VectorNoiseKernel.INSTANCE.isAvailable()) {
            vectorStatus = "available";
        } else {
            vectorStatus = "implementation failed to load; scalar fallback";
        }
        String message = "Noise kernels: scalar=" + Config.SCALAR_NOISE.get()
                + ", vector=" + Config.VECTOR_NOISE.get()
                + " (Vector API " + vectorStatus + "). Adaptive render distance="
                + Config.ADAPTIVE_RENDER_DISTANCE.get() + "; thresholds="
                + Config.TARGET_FPS.get() + "/" + (Config.TARGET_FPS.get() + Config.RECOVERY_FPS_MARGIN.get())
                + " FPS, samples=" + Config.DOWN_SAMPLES.get() + "/" + Config.UP_SAMPLES.get()
                + ", cooldown=" + Config.COOLDOWN_MS.get() + "ms.";
        source.sendSuccess(() -> Component.literal(message), false);
        Whyissofastnow.LOGGER.info(message);
        return 1;
    }

    private static int runBenchmark(CommandSourceStack source) {
        double[] xs = new double[BATCH];
        double[] ys = new double[BATCH];
        double[] zs = new double[BATCH];
        double[] output = new double[BATCH];
        NoiseBenchmarkData.fillCoordinates(NoiseBenchmarkData.SEED, xs, ys, zs);

        NoiseKernel scalar = ScalarNoiseKernel.INSTANCE;
        NoiseKernel vector = VectorNoiseKernel.INSTANCE;
        for (int iteration = 0; iteration < WARMUP_ITERATIONS; iteration++) {
            scalar.sample(xs, ys, zs, output);
            if (vector.isAvailable()) {
                vector.sample(xs, ys, zs, output);
            }
        }

        Timing scalarTiming = measure(scalar, xs, ys, zs, output);
        if (!vector.isAvailable()) {
            String message = "Benchmark seed=0xC0FFEE BATCH=256: scalar="
                    + formatMillis(scalarTiming.elapsedNanos())
                    + "ms; vector unavailable (use --add-modules=jdk.incubator.vector).";
            source.sendSuccess(() -> Component.literal(message), false);
            Whyissofastnow.LOGGER.info(message);
            return 1;
        }

        Timing vectorTiming = measure(vector, xs, ys, zs, output);
        double speedup = (double) scalarTiming.elapsedNanos() / vectorTiming.elapsedNanos();
        String message = "Benchmark seed=0xC0FFEE BATCH=256: scalar="
                + formatMillis(scalarTiming.elapsedNanos())
                + "ms vector=" + formatMillis(vectorTiming.elapsedNanos())
                + "ms speedup=" + String.format(Locale.ROOT, "%.2f", speedup) + "x";
        source.sendSuccess(() -> Component.literal(message), false);
        Whyissofastnow.LOGGER.info(message);
        return 1;
    }

    private static Timing measure(NoiseKernel kernel, double[] xs, double[] ys, double[] zs, double[] output) {
        long start = System.nanoTime();
        for (int iteration = 0; iteration < MEASUREMENT_ITERATIONS; iteration++) {
            kernel.sample(xs, ys, zs, output);
        }
        long elapsed = System.nanoTime() - start;
        double checksum = 0.0;
        for (double value : output) {
            checksum += value;
        }
        benchmarkSink = checksum;
        return new Timing(elapsed);
    }

    private static String formatMillis(long nanoseconds) {
        return String.format(Locale.ROOT, "%.3f", nanoseconds / 1_000_000.0);
    }

    private record Timing(long elapsedNanos) {
    }
}
