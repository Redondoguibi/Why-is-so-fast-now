package com.redondoguibi.whyisofastnow.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

import com.redondoguibi.whyisofastnow.Config;
import com.redondoguibi.whyisofastnow.NoiseKernel;
import com.redondoguibi.whyisofastnow.NoiseKernels;
import com.redondoguibi.whyisofastnow.ScalarNoiseKernel;

@Mixin(ImprovedNoise.class)
public abstract class ImprovedNoiseMixin {
    @Redirect(
            method = "noise(DDDDD)D",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/synth/ImprovedNoise;sampleAndLerp(IIIDDDD)D"))
    private double whyissofastnow$sampleNoise(
            ImprovedNoise noise,
            int gridX,
            int gridY,
            int gridZ,
            double deltaX,
            double weirdDeltaY,
            double deltaZ,
            double deltaY) {
        ImprovedNoiseAccessor accessor = (ImprovedNoiseAccessor) (Object) noise;
        if (Config.VECTOR_NOISE.get()) {
            NoiseKernel vectorKernel = NoiseKernels.vectorKernel();
            if (vectorKernel != null) {
                return vectorKernel.sample(
                        accessor.whyissofastnow$getPermutation(),
                        gridX,
                        gridY,
                        gridZ,
                        deltaX,
                        weirdDeltaY,
                        deltaZ,
                        deltaY);
            }
        }
        if (Config.SCALAR_NOISE.get()) {
            return ScalarNoiseKernel.INSTANCE.sample(
                    accessor.whyissofastnow$getPermutation(),
                    gridX,
                    gridY,
                    gridZ,
                    deltaX,
                    weirdDeltaY,
                    deltaZ,
                    deltaY);
        }
        return accessor.whyissofastnow$invokeSampleAndLerp(
                gridX,
                gridY,
                gridZ,
                deltaX,
                weirdDeltaY,
                deltaZ,
                deltaY);
    }
}
