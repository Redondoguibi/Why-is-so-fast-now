package com.redondoguibi.whyisofastnow;

/**
 * Describes a separately configurable optimization hook.
 *
 * <p>Centralizing enable checks lets the project add measured hot paths consistently without
 * turning optional optimizations on implicitly.</p>
 */
public interface HotPath {
    /** Stable identifier for logs and registry diagnostics. */
    String id();

    /** Whether current config and runtime capabilities permit this path. */
    boolean shouldEnable();

    /** Applies or refreshes this path after its enable condition is satisfied. */
    void apply();
}
