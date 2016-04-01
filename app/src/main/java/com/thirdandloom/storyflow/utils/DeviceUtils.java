package com.thirdandloom.storyflow.utils;

import com.thirdandloom.storyflow.StoryflowApplication;

import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

public class DeviceUtils extends BaseUtils {
    public static DisplayMetrics getDisplayMetrics() {
        return getResources().getDisplayMetrics();
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

    public static boolean isLollipopOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = StoryflowApplication.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result =  StoryflowApplication.getInstance().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeight() {
        int resourceId = getNavigationBarResourceId();
        if (hasMenuKey() || hasHardwareBackKey() || resourceId <= 0) {
            return 0;
        }
        return getResources().getDimensionPixelSize(resourceId);
    }

    public static int getNavigationBarResourceId() {
        return getResources().getIdentifier("navigation_bar_height", "dimen", "android");
    }

    public static boolean hasMenuKey() {
        return ViewConfiguration.get(getContext()).hasPermanentMenuKey();
    }

    public static boolean hasHardwareBackKey() {
        return KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    }

}
