package com.redondoguibi.whyisofastnow;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.common.Mod;

@Mod(Whyissofastnow.MODID)
public class Whyissofastnow {
    public static final String MODID = "whyissofastnow";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Whyissofastnow(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        HotPathRegistry.GLOBAL.register(new NoiseKernelHotPath());
        LOGGER.info("Why is so fast now? loaded");
    }
}
