package com.redondoguibi.whyisofastnow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdaptiveRenderControllerTest {
    @Test
    void cooldownPreventsRapidConsecutiveDistanceChanges() {
        AdaptiveRenderController controller = new AdaptiveRenderController(45, 58, 2, 2, 5_000);

        assertEquals(8, controller.update(30, 8, 4, 12, 0));
        assertEquals(7, controller.update(30, 8, 4, 12, 1_000));
        assertEquals(7, controller.update(30, 7, 4, 12, 2_000));
        assertEquals(7, controller.update(30, 7, 4, 12, 5_999));
        assertEquals(6, controller.update(30, 7, 4, 12, 6_000));
    }

    @Test
    void deadZoneGraduallyDecaysLowFpsStreak() {
        AdaptiveRenderController controller = new AdaptiveRenderController(45, 58, 3, 10, 0);

        assertEquals(8, controller.update(30, 8, 4, 12, 0));
        assertEquals(8, controller.update(50, 8, 4, 12, 1_000));
        assertEquals(8, controller.update(30, 8, 4, 12, 2_000));
        assertEquals(8, controller.update(30, 8, 4, 12, 3_000));
        assertEquals(7, controller.update(30, 8, 4, 12, 4_000));
    }
}
