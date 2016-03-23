package com.thirdandloom.storyflow.utils;

import android.graphics.Color;

public class ColorUtils {

    public static int getNegativeColor(int previousColor, int percent) {
        long addedValue = Math.round(2.55 * percent);

        long previousRed = (previousColor >> 16);
        long previousGreen = (previousColor >> 8 & 0x00FF);
        long previousBlue = (previousColor >> 32 & 0x0000FF);

        int newRed = previousRed - addedValue > 255
                ? 255 :
                previousRed - addedValue < 0
                        ? 0 : (int)(previousRed - addedValue);

        int newGreen = previousGreen - addedValue > 255
                ? 255 :
                previousGreen - addedValue < 0
                        ? 0 : (int)(previousGreen - addedValue);

        int newBlue = previousBlue - addedValue > 255
                ? 255 :
                previousBlue - addedValue < 0
                        ? 0 : (int)(previousBlue - addedValue);

        int newColor = Color.rgb(newRed, newGreen, newBlue);
        return newColor;
    }
}
