package com.thirdandloom.storyflow.utils;

import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

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
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result =  getResources().getDimensionPixelSize(resourceId);
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
        return ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey();
    }

    public static boolean hasHardwareBackKey() {
        return KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    }

    public static int getViewInset(View view) {
        if (view == null || Build.VERSION.SDK_INT < 21 || view.getHeight() == AndroidUtils.displaySize.y || view.getHeight() == AndroidUtils.displaySize.y - DeviceUtils.getStatusBarHeight()) {
            return 0;
        }
        try {
            Field mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
            mAttachInfoField.setAccessible(true);
            Object mAttachInfo = mAttachInfoField.get(view);
            if (mAttachInfo != null) {
                Field mStableInsetsField = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                mStableInsetsField.setAccessible(true);
                Rect insets = (Rect)mStableInsetsField.get(mAttachInfo);
                return insets.bottom;
            }
        } catch (Exception e) {
            exception(e);
        }
        return 0;
    }

    public static void updateStatusBarColor(Window window, int color) {
        if (DeviceUtils.isLollipopOrHigher()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}
