package com.thirdandloom.storyflow.utils.animations;

import android.view.View;
import android.view.animation.TranslateAnimation;

public class ShowHeaderAnimation extends TranslateAnimation {
    public ShowHeaderAnimation(View header, int duration) {
        super(0, 0, header.getY() - header.getMeasuredHeight(), 0);
        setDuration(duration);
        AnimationListener listener = new ShowViewAnimationListener(header);
        setAnimationListener(listener);
    }
}
