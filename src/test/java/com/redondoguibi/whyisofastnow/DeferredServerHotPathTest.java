package com.redondoguibi.whyisofastnow;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DeferredServerHotPathTest {
    @Test
    void serverCandidatesRemainInertWithoutSparkEvidence() {
        List<DeferredServerHotPath> candidates = List.of(
                new NaturalSpawnerHotPath(),
                new BiomeManagerHotPath(),
                new FeatureContextHotPath(),
                new BlockableEventLoopHotPath());
        HotPathRegistry registry = new HotPathRegistry();
        candidates.forEach(registry::register);
        List<HotPath> applied = new ArrayList<>();

        candidates.forEach(candidate -> {
            assertFalse(candidate.shouldEnable(), candidate.id());
            assertFalse(candidate.evidence().isPresent(), candidate.id());
            candidate.apply();
        });
        registry.forEachEnabled(applied::add);

        assertEquals(List.of(
                "natural-spawner",
                "biome-manager",
                "feature-context",
                "blockable-event-loop"),
                candidates.stream().map(HotPath::id).toList());
        assertEquals(List.of(), applied);
    }
}
