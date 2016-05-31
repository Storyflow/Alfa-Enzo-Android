package com.thirdandloom.storyflow.utils.animations;

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
        return footer.getY() - footer.getHeight();
    }
}
