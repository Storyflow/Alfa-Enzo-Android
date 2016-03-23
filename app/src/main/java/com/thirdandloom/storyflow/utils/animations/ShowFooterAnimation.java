package com.thirdandloom.storyflow.utils.animations;

import android.view.View;
import android.view.animation.TranslateAnimation;

public class ShowFooterAnimation extends TranslateAnimation {
    public ShowFooterAnimation(View footer, int duration) {
        super(0, 0, footer.getY(), 0);
        setDuration(duration);
        AnimationListener listener = new ShowViewAnimationListener(footer);
        setAnimationListener(listener);
    }
}
