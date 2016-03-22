package com.thirdandloom.storyflow.utils;

import com.thirdandloom.storyflow.StoryflowApplication;

import android.util.DisplayMetrics;

public class DeviceUtils {
    public static DisplayMetrics getDisplayMetrics() {
        return StoryflowApplication.getInstance().getResources().getDisplayMetrics();
    }

    public static int getDisplayWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public static int getDisplayHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public static int getLargerDisplaySize() {
        return Math.max(getDisplayHeight(), getDisplayWidth());
    }
}
