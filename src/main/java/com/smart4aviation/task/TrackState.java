package com.smart4aviation.task;

public class TrackState {
    long currentP = 0;
    long totalHistory = 0;
    long lastUpdateTime = 0;
    boolean isActive = false;

    long calculateCurrentTotal(long currentTime) {
        if (!isActive) return 0;
        return totalHistory + (currentP * (currentTime - lastUpdateTime));
    }
}