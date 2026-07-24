package com.prygin.screenshake;

import java.util.Random;

public class ScreenShakeManager {
    private static float intensity = 0f;
    private static int duration = 0;
    private static final Random RANDOM = new Random();

    public static void shake(float intensity, int durationTicks) {
        ScreenShakeManager.intensity = intensity;
        ScreenShakeManager.duration = durationTicks;
    }

    public static float getYawOffset() {
        if (duration <= 0) return 0f;
        return (RANDOM.nextFloat() * 2 - 1) * intensity;
    }

    public static float getPitchOffset() {
        if (duration <= 0) return 0f;
        return (RANDOM.nextFloat() * 2 - 1) * intensity;
    }

    public static void tick() {
        if (duration > 0) {
            duration--;
            intensity *= 0.9f;
        } else {
            intensity = 0;
        }
    }
}