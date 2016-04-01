package com.thirdandloom.storyflow.utils;

public class MathUtils extends BaseUtils {

    public static float calculateMinScaleRatio(int realWidth, int realHeight, int boxWidth, int boxHeight) {
        float scaleHeight = (float) boxWidth / realWidth;
        float scaleWidth = (float) boxHeight / realHeight;
        return Math.min(scaleHeight, scaleWidth);
    }

    public static float calculateMaxScaleRatio(int realWidth, int realHeight, int boxWidth, int boxHeight) {
        float scaleHeight = (float) boxWidth / realWidth;
        float scaleWidth = (float) boxHeight / realHeight;
        return Math.max(scaleHeight, scaleWidth);
    }
}
