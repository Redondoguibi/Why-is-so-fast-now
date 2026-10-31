package com.redondoguibi.whyisofastnow;

/**
 * Pure state machine for gradually adjusting render distance from sampled FPS.
 *
 * <p>Separate low/high thresholds and a cooldown prevent oscillation when frame rate hovers
 * near the target; dead-zone samples decay streaks instead of erasing them abruptly.</p>
 */
public final class AdaptiveRenderController {
    private final int lowFpsThreshold;
    private final int highFpsThreshold;
    private final int downSamples;
    private final int upSamples;
    private final long cooldownMs;
    private int downStreak;
    private int upStreak;
    private long lastChangeMs = Long.MIN_VALUE;

    /** Creates a controller with the project's conservative 45/58 FPS defaults. */
    public AdaptiveRenderController(int downSamples, int upSamples, long cooldownMs) {
        this(45, 58, downSamples, upSamples, cooldownMs);
    }

    /** Creates a controller with explicit thresholds and timing, useful for config and tests. */
    public AdaptiveRenderController(
            int lowFpsThreshold,
            int highFpsThreshold,
            int downSamples,
            int upSamples,
            long cooldownMs) {
        if (lowFpsThreshold < 0 || highFpsThreshold <= lowFpsThreshold
                || downSamples < 1 || upSamples < 1 || cooldownMs < 0) {
            throw new IllegalArgumentException("Invalid adaptive render controller settings.");
        }
        this.lowFpsThreshold = lowFpsThreshold;
        this.highFpsThreshold = highFpsThreshold;
        this.downSamples = downSamples;
        this.upSamples = upSamples;
        this.cooldownMs = cooldownMs;
    }

    /**
     * Returns the next distance, changing it by at most one chunk when streak and cooldown allow.
     */
    public int update(int fps, int currentDistance, int minimumDistance, int preferredDistance, long nowMs) {
        if (fps < lowFpsThreshold) {
            downStreak = Math.min(downSamples, downStreak + 1);
            upStreak = decay(upStreak);
        } else if (fps > highFpsThreshold) {
            upStreak = Math.min(upSamples, upStreak + 1);
            downStreak = decay(downStreak);
        } else {
            downStreak = decay(downStreak);
            upStreak = decay(upStreak);
        }

        if (lastChangeMs != Long.MIN_VALUE && nowMs - lastChangeMs < cooldownMs) {
            return currentDistance;
        }
        if (downStreak >= downSamples && currentDistance > minimumDistance) {
            lastChangeMs = nowMs;
            clearStreaks();
            return currentDistance - 1;
        }
        if (upStreak >= upSamples && currentDistance < preferredDistance) {
            lastChangeMs = nowMs;
            clearStreaks();
            return currentDistance + 1;
        }
        return currentDistance;
    }

    /** Clears accumulated samples when the player changes options or leaves the world. */
    public void reset() {
        clearStreaks();
        lastChangeMs = Long.MIN_VALUE;
    }

    private static int decay(int streak) {
        return Math.max(0, streak - 1);
    }

    private void clearStreaks() {
        downStreak = 0;
        upStreak = 0;
    }
}
