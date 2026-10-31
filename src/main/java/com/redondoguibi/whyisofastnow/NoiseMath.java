package com.redondoguibi.whyisofastnow;

final class NoiseMath {
    private NoiseMath() {
    }

    static int floor(double value) {
        return (int) Math.floor(value);
    }

    static double smoothstep(double value) {
        return value * value * value * (value * (value * 6.0 - 15.0) + 10.0);
    }

    static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    static double lerp3(
            double x,
            double y,
            double z,
            double v000,
            double v100,
            double v010,
            double v110,
            double v001,
            double v101,
            double v011,
            double v111) {
        return lerp(
                z,
                lerp(y, lerp(x, v000, v100), lerp(x, v010, v110)),
                lerp(y, lerp(x, v001, v101), lerp(x, v011, v111)));
    }
}
