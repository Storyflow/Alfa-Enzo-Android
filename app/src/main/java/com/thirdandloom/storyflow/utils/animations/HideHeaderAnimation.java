package com.thirdandloom.storyflow.utils.animations;

import android.view.View;
import android.view.animation.TranslateAnimation;

public class HideHeaderAnimation extends TranslateAnimation {
    public HideHeaderAnimation(View header, int duration) {
        super(0, 0, 0, header.getY() - header.getHeight());
        setDuration(duration);
        AnimationListener listener = new HideViewAnimationListener(header);
        setAnimationListener(listener);
    }
}
