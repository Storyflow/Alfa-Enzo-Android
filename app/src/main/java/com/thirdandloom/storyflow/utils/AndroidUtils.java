package com.thirdandloom.storyflow.utils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class AndroidUtils extends BaseUtils {

    public static Point displaySize = new Point();
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static float density = 1;
    public static ViewConfiguration viewConfiguration;

    static {
        density = getResources().getDisplayMetrics().density;
        viewConfiguration = ViewConfiguration.get(getApplicationContext());
        checkDisplaySize();
    }

    public static Rect getWindowVisibleRect(Window window) {
        Rect windowVisibleRect = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(windowVisibleRect);
        return windowVisibleRect;
    }

    public static int minScrollPx() {
        return viewConfiguration.getScaledTouchSlop();
    }

    public static int minVelocityPxPerSecond() {
        return viewConfiguration.getScaledMinimumFlingVelocity();
    }

    public static int maxVelocityPxPerSecond() {
        return viewConfiguration.getScaledMaximumFlingVelocity();
    }

    public static int longPressTimeout() {
        return viewConfiguration.getLongPressTimeout();
    }

    public static void showKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int)Math.ceil(density * value);
    }

    public static void checkDisplaySize() {
        try {
            WindowManager manager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                }
            }
        } catch (Exception e) {
            exception(e);
        }
    }
}
