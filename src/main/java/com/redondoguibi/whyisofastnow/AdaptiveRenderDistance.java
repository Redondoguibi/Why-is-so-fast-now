package com.redondoguibi.whyisofastnow;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Samples client FPS and applies the adaptive controller's gradual distance changes.
 *
 * <p>The user's preferred distance is restored when the feature is disabled or the world closes.</p>
 */
@EventBusSubscriber(modid = Whyissofastnow.MODID, value = Dist.CLIENT)
public final class AdaptiveRenderDistance {
    private static final int SAMPLE_INTERVAL_TICKS = 20;

    private static int ticksUntilSample;
    private static int preferredRenderDistance = -1;
    private static int lastAppliedRenderDistance = -1;
    private static ControllerSettings controllerSettings;
    private static AdaptiveRenderController controller;

    private AdaptiveRenderDistance() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || !Config.ADAPTIVE_RENDER_DISTANCE.get()) {
            reset(minecraft);
            return;
        }

        if (++ticksUntilSample < SAMPLE_INTERVAL_TICKS) {
            return;
        }
        ticksUntilSample = 0;

        int currentDistance = minecraft.options.renderDistance().get();
        if (lastAppliedRenderDistance < 0 || currentDistance != lastAppliedRenderDistance) {
            preferredRenderDistance = currentDistance;
            lastAppliedRenderDistance = currentDistance;
            configureController();
            controller.reset();
        }
        configureController();

        int fps = minecraft.getFps();
        if (fps <= 0) {
            return;
        }
        int minimum = Math.min(Config.MIN_RENDER_DISTANCE.get(), preferredRenderDistance);
        int nextDistance = controller.update(
                fps,
                currentDistance,
                minimum,
                preferredRenderDistance,
                System.nanoTime() / 1_000_000L);
        if (nextDistance != currentDistance) {
            minecraft.options.renderDistance().set(nextDistance);
            lastAppliedRenderDistance = nextDistance;
        }
    }

    private static void configureController() {
        ControllerSettings next = new ControllerSettings(
                Config.TARGET_FPS.get(),
                Config.TARGET_FPS.get() + Config.RECOVERY_FPS_MARGIN.get(),
                Config.DOWN_SAMPLES.get(),
                Config.UP_SAMPLES.get(),
                Config.COOLDOWN_MS.get());
        if (!next.equals(controllerSettings)) {
            controllerSettings = next;
            controller = new AdaptiveRenderController(
                    next.lowThreshold(),
                    next.highThreshold(),
                    next.downSamples(),
                    next.upSamples(),
                    next.cooldownMs());
        }
    }

    private static void reset(Minecraft minecraft) {
        if (preferredRenderDistance >= 0
                && lastAppliedRenderDistance >= 0
                && minecraft.options.renderDistance().get() == lastAppliedRenderDistance) {
            minecraft.options.renderDistance().set(preferredRenderDistance);
        }
        ticksUntilSample = 0;
        preferredRenderDistance = -1;
        lastAppliedRenderDistance = -1;
        if (controller != null) {
            controller.reset();
        }
    }

    private record ControllerSettings(
            int lowThreshold,
            int highThreshold,
            int downSamples,
            int upSamples,
            int cooldownMs) {
    }
}
