package com.prygin.zoom;

public class ZoomManager {

    private static float currentZoomScale = 1.0F;
    private static float targetZoomScale = 1.0F;

    private static final float SMOOTHING = 0.3F;
    private static final float MIN_SCALE = 0.01F; // guards against divide-by-huge-fov weirdness

    private ZoomManager() {}

    /**
     * @param scale FOV multiplier. 1.0F = no zoom, 0.1F = 10x zoom,
     *              0.25F = 4x zoom, etc. Lower = more zoomed in.
     */
    public static void setZoom(float scale) {
        targetZoomScale = Math.max(MIN_SCALE, Math.min(1.0F, scale));
    }

    public static void resetZoom() {
        targetZoomScale = 1.0F;
    }

    public static boolean isZooming() {
        return targetZoomScale < 1.0F;
    }

    public static float getSmoothedZoomScale() {
        currentZoomScale += (targetZoomScale - currentZoomScale) * SMOOTHING;
        return currentZoomScale;
    }
}