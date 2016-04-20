package com.thirdandloom.storyflow.utils;

import android.graphics.Color;
import android.support.annotation.ColorRes;

import java.util.Random;

public class ColorUtils extends BaseUtils {

    public static int color(@ColorRes int colorRes) {
        return getResources().getColor(colorRes);
    }

    public static int getRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static int calculateStatusBarColor(int color) {
        return getNegativeColor(color, 10);
    }

    public static int getNegativeColor(int previousColor, int percent) {
        String hexColor = String.format("#%06X", (0xFFFFFF & previousColor));
        previousColor = Color.parseColor(hexColor);

        long addedValue = Math.round(2.55 * percent);
        long previousRed = Color.red(previousColor);
        long previousGreen = Color.green(previousColor);
        long previousBlue =  Color.blue(previousColor);

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
