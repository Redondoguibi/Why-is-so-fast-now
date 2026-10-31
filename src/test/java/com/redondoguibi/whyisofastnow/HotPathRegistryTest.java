package com.redondoguibi.whyisofastnow;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private static HotPath path(String id, boolean enabled) {
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
        };
    }
}
