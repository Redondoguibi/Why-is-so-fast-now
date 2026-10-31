package com.redondoguibi.whyisofastnow;

/**
 * Deferred server BlockableEventLoop candidate.
 *
 * <p>Its queue ordering, wakeups and thread-affinity are concurrency contracts, so changing task
 * scheduling without a measured bottleneck and dedicated stress/parity tests is unsafe.</p>
 */
public final class BlockableEventLoopHotPath extends DeferredServerHotPath {
    public BlockableEventLoopHotPath() {
        super("blockable-event-loop", Config.BLOCKABLE_EVENT_LOOP_HOT_PATH);
    }
}
