package com.redondoguibi.whyisofastnow;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

@Mod(Whyissofastnow.MODID)
public class Whyissofastnow {
    public static final String MODID = "whyissofastnow";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Whyissofastnow(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(ModConfigEvent.Loading.class, event -> onConfig(event));
        modEventBus.addListener(ModConfigEvent.Reloading.class, event -> onConfig(event));
        LOGGER.info("Why is so fast now? loaded");
    }

    private static void onConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() == Config.SPEC) {
            NoisePolicy.refresh();
        }
    }
}
