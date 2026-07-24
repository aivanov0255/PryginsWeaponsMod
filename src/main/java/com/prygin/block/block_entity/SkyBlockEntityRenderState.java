package com.prygin.block.block_entity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class SkyBlockEntityRenderState extends BlockEntityRenderState {

    private int time = 0;

    // bit i set (i = Direction#get3DDataValue(): 0=DOWN,1=UP,2=NORTH,3=SOUTH,4=WEST,5=EAST)
    // means that face should be drawn (no matching sky block neighbor on that side)
    private int visibleFaces = 0b111111;

    // color per corner, indexed by (dx | dy<<1 | dz<<2) where each of dx/dy/dz is 0 (min) or 1 (max)
    private int[] cornerColors = new int[8];

    // rotation (degrees) driving sun/moon position around the block, derived from timeOfDay
    private float celestialAngle = 0f;

    // precomputed, camera-billboarded star quad corners in block-local space,
    // 12 floats per star (4 vertices * xyz), laid out flat
    private float[] starVertices = new float[0];

    // ARGB, alpha already baked in (0 alpha = fully hidden), tinted by current sky color
    private int sunColor = 0xFFFFFFFF;
    private int moonColor = 0xFFFFFFFF;
    private int starColor = 0xFFFFFFFF;

    // UV rect into the celestials atlas: [u0, v0, u1, v1]
    private float[] sunUv = new float[] {0f, 0f, 1f, 1f};
    private float[] moonUv = new float[] {0f, 0f, 1f, 1f};

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getVisibleFaces() {
        return this.visibleFaces;
    }

    public void setVisibleFaces(int visibleFaces) {
        this.visibleFaces = visibleFaces;
    }

    public int[] getCornerColors() {
        return this.cornerColors;
    }

    public void setCornerColors(int[] cornerColors) {
        this.cornerColors = cornerColors;
    }

    public float getCelestialAngle() {
        return this.celestialAngle;
    }

    public void setCelestialAngle(float celestialAngle) {
        this.celestialAngle = celestialAngle;
    }

    public float[] getStarVertices() {
        return this.starVertices;
    }

    public void setStarVertices(float[] starVertices) {
        this.starVertices = starVertices;
    }

    public int getSunColor() {
        return this.sunColor;
    }

    public void setSunColor(int sunColor) {
        this.sunColor = sunColor;
    }

    public int getMoonColor() {
        return this.moonColor;
    }

    public void setMoonColor(int moonColor) {
        this.moonColor = moonColor;
    }

    public int getStarColor() {
        return this.starColor;
    }

    public void setStarColor(int starColor) {
        this.starColor = starColor;
    }

    public float[] getSunUv() {
        return this.sunUv;
    }

    public void setSunUv(float[] sunUv) {
        this.sunUv = sunUv;
    }

    public float[] getMoonUv() {
        return this.moonUv;
    }

    public void setMoonUv(float[] moonUv) {
        this.moonUv = moonUv;
    }
}