package com.redondoguibi.whyisofastnow;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HotPathRegistryTest {
    @Test
    void forEachEnabledVisitsOnlyEnabledPathsInRegistrationOrder() {
        HotPathRegistry registry = new HotPathRegistry();
        registry.register(path("first", true));
        registry.register(path("disabled", false));
        registry.register(path("last", true));
        List<String> visited = new ArrayList<>();

        registry.forEachEnabled(hotPath -> visited.add(hotPath.id()));

        assertEquals(List.of("first", "last"), visited);
    }

    @Test
    void duplicateIdsAreRejected() {
        HotPathRegistry registry = new HotPathRegistry();
        registry.register(path("noise", true));

        assertThrows(IllegalArgumentException.class, () -> registry.register(path("noise", false)));
    }

    @Test
    void forEachIncludesDisabledPathsAndTheirEvidence() {
        HotPathRegistry registry = new HotPathRegistry();
        HotPathEvidence evidence = new HotPathEvidence(
                "https://spark.lucko.me/example",
                "noise occupied 31% of sampled generation CPU",
                "implement behind a disabled-by-default toggle",
                "NoiseKernelParityTest");
        HotPath candidate = path("noise", false, evidence);
        registry.register(candidate);
        List<HotPath> visited = new ArrayList<>();

        registry.forEach(visited::add);

        assertEquals(List.of(candidate), visited);
        assertEquals(evidence, visited.getFirst().evidence().orElseThrow());
        assertTrue(new NoiseKernelHotPath().evidence().isEmpty());
    }

    private static HotPath path(String id, boolean enabled) {
        return path(id, enabled, null);
    }

    private static HotPath path(String id, boolean enabled, HotPathEvidence evidence) {
        return new HotPath() {
            @Override
            public String id() {
                return id;
            }

            @Override
            public boolean shouldEnable() {
                return enabled;
            }

            @Override
            public void apply() {
            }

            @Override
            public java.util.Optional<HotPathEvidence> evidence() {
                return java.util.Optional.ofNullable(evidence);
            }
        };
    }
}
