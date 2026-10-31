package com.redondoguibi.whyisofastnow.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

@Mixin(ImprovedNoise.class)
public interface ImprovedNoiseAccessor {
    @Accessor("p")
    byte[] whyissofastnow$getPermutation();

    @Invoker("sampleAndLerp")
    double whyissofastnow$invokeSampleAndLerp(int gridX, int gridY, int gridZ, double deltaX, double weirdDeltaY, double deltaZ, double deltaY);
}
