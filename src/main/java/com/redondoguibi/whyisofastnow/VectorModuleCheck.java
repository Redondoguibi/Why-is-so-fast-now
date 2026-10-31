package com.redondoguibi.whyisofastnow;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Reports Vector API availability during common setup without requiring that optional module.
 *
 * <p>Early diagnostics explain why a requested SIMD kernel may be using its scalar fallback.</p>
 */
@EventBusSubscriber(modid = Whyissofastnow.MODID, bus = EventBusSubscriber.Bus.MOD)
@SuppressWarnings("removal")
public final class VectorModuleCheck {
    private VectorModuleCheck() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        if (ModuleLayer.boot().findModule("jdk.incubator.vector").isEmpty()) {
            Whyissofastnow.LOGGER.warn(
                    "Java Vector API is unavailable; vector noise will use the scalar fallback. "
                            + "Enable it with --add-modules=jdk.incubator.vector.");
        } else if (VectorNoiseKernel.INSTANCE.isAvailable()) {
            Whyissofastnow.LOGGER.info("Java Vector API is available for the optional noise kernel.");
        } else {
            Whyissofastnow.LOGGER.warn(
                    "Java Vector API was resolved, but its noise kernel failed to load; using scalar fallback.",
                    VectorNoiseKernel.INSTANCE.loadFailure());
        }
    }
}
