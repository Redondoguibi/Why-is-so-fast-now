package com.redondoguibi.whyisofastnow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Holds the mod's optimization hooks in one place.
 *
 * <p>The registry snapshots its entries during iteration so registrations cannot corrupt a
 * config-reload traversal.</p>
 */
public final class HotPathRegistry {
    public static final HotPathRegistry GLOBAL = new HotPathRegistry();

    private final List<HotPath> paths = new ArrayList<>();

    /** Registers one path and rejects duplicate identifiers to keep diagnostics unambiguous. */
    public synchronized void register(HotPath hotPath) {
        Objects.requireNonNull(hotPath, "hotPath");
        if (paths.stream().anyMatch(existing -> existing.id().equals(hotPath.id()))) {
            throw new IllegalArgumentException("Hot path already registered: " + hotPath.id());
        }
        paths.add(hotPath);
    }

    /** Visits only enabled paths in registration order. */
    public void forEachEnabled(Consumer<HotPath> consumer) {
        Objects.requireNonNull(consumer, "consumer");
        List<HotPath> snapshot;
        synchronized (this) {
            snapshot = List.copyOf(paths);
        }
        snapshot.stream().filter(HotPath::shouldEnable).forEach(consumer);
    }
}
