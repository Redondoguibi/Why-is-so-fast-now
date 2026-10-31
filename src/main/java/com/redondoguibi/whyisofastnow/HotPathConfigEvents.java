package com.redondoguibi.whyisofastnow;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = Whyissofastnow.MODID, bus = EventBusSubscriber.Bus.MOD)
@SuppressWarnings("removal")
public final class HotPathConfigEvents {
    private HotPathConfigEvents() {
    }

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        refreshHotPaths();
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        refreshHotPaths();
    }

    private static void refreshHotPaths() {
        NoiseKernels.clearActiveKernel();
        HotPathRegistry.GLOBAL.forEachEnabled(HotPath::apply);
    }
}
