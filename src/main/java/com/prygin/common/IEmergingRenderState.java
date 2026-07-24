package com.prygin.common;

public interface IEmergingRenderState {
    boolean isEmerging();
    void setEmerging(boolean emerging);

    float getEmergeProgress();
    void setEmergeProgress(float progress);
}
