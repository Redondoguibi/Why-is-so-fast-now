package com.redondoguibi.whyisofastnow;

import java.util.Optional;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Represents a server optimization candidate that has not passed profile and equivalence gates.
 *
 * <p>These candidates intentionally perform no transformation: without a Spark baseline and a
 * proven replacement, registering an enabled-looking optimization would violate the project's
 * measurement-first and vanilla-fallback guarantees.</p>
 */
public abstract class DeferredServerHotPath implements HotPath {
    private final String id;
    private final ModConfigSpec.BooleanValue toggle;

    protected DeferredServerHotPath(String id, ModConfigSpec.BooleanValue toggle) {
        this.id = id;
        this.toggle = toggle;
    }

    @Override
    public final String id() {
        return id;
    }

    @Override
    public final boolean shouldEnable() {
        return evidence().isPresent() && toggle.get();
    }

    @Override
    public final void apply() {
        // A candidate has no behavior until its profiled replacement is implemented and tested.
    }

    @Override
    public final Optional<HotPathEvidence> evidence() {
        return Optional.empty();
    }
}
