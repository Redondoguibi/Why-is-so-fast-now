package com.redondoguibi.whyisofastnow;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = Whyissofastnow.MODID, value = Dist.CLIENT)
public final class AdaptiveRenderDistance {
    private static final int SAMPLE_INTERVAL_TICKS = 20;
    private static final int LOW_FPS_SAMPLES_TO_REDUCE = 3;
    private static final int HIGH_FPS_SAMPLES_TO_RESTORE = 10;

    private static int ticksUntilSample;
    private static int lowFpsSamples;
    private static int highFpsSamples;
    private static int preferredRenderDistance = -1;
    private static int lastAppliedRenderDistance = -1;

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
            lowFpsSamples = 0;
            highFpsSamples = 0;
        }

        int fps = minecraft.getFps();
        if (fps <= 0) {
            return;
        }

        if (fps < Config.TARGET_FPS.get()) {
            lowFpsSamples++;
            highFpsSamples = 0;
            if (lowFpsSamples >= LOW_FPS_SAMPLES_TO_REDUCE) {
                int minimum = Math.min(Config.MIN_RENDER_DISTANCE.get(), preferredRenderDistance);
                if (currentDistance > minimum) {
                    applyDistance(minecraft, currentDistance - 1);
                }
                lowFpsSamples = 0;
            }
            return;
        }

        lowFpsSamples = 0;
        int recoveryFps = Config.TARGET_FPS.get() + Config.RECOVERY_FPS_MARGIN.get();
        if (fps >= recoveryFps && currentDistance < preferredRenderDistance) {
            highFpsSamples++;
            if (highFpsSamples >= HIGH_FPS_SAMPLES_TO_RESTORE) {
                applyDistance(minecraft, currentDistance + 1);
                highFpsSamples = 0;
            }
        } else {
            highFpsSamples = 0;
        }
    }

    private static void applyDistance(Minecraft minecraft, int distance) {
        minecraft.options.renderDistance().set(distance);
        lastAppliedRenderDistance = distance;
    }

    private static void reset(Minecraft minecraft) {
        if (preferredRenderDistance >= 0
                && lastAppliedRenderDistance >= 0
                && minecraft.options.renderDistance().get() == lastAppliedRenderDistance) {
            minecraft.options.renderDistance().set(preferredRenderDistance);
        }
        ticksUntilSample = 0;
        lowFpsSamples = 0;
        highFpsSamples = 0;
        preferredRenderDistance = -1;
        lastAppliedRenderDistance = -1;
    }
}
