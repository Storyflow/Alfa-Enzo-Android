package com.thirdandloom.storyflow.utils.animations;


import com.thirdandloom.storyflow.utils.DeviceUtils;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.TranslateAnimation;

public class HideFooterAnimation extends TranslateAnimation {
    public HideFooterAnimation(View footer, int duration) {
        super(0, 0, 0, getToYDelta(footer));
        setDuration(duration);
        AnimationListener listener = new HideViewAnimationListener(footer);
        setAnimationListener(listener);
    }

    private static float getToYDelta(View footer) {
        DisplayMetrics displaymetrics = DeviceUtils.getDisplayMetrics();
        return displaymetrics.heightPixels - footer.getY();
    }
}
