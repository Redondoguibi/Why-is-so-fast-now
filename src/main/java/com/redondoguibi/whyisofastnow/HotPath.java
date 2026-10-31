package com.redondoguibi.whyisofastnow;

import java.util.Optional;

/**
 * Describes a separately configurable optimization hook.
 *
 * <p>Centralizing enable checks and profile evidence lets the project add measured hot paths
 * consistently without turning optional optimizations on implicitly.</p>
 */
public interface HotPath {
    /** Stable identifier for logs and registry diagnostics. */
    String id();

    /** Whether current config and runtime capabilities permit this path. */
    boolean shouldEnable();

    /** Applies or refreshes this path after its enable condition is satisfied. */
    void apply();

    /**
     * Returns the Spark observation and correctness decision supporting this optimization.
     *
     * <p>Empty means no profile evidence has been recorded; callers must not infer that the
     * optimization was measured just because it is registered.</p>
     */
    default Optional<HotPathEvidence> evidence() {
        return Optional.empty();
    }
}
