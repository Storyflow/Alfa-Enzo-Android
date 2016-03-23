package com.thirdandloom.storyflow.utils.animations;

import com.thirdandloom.storyflow.utils.ViewUtils;

import android.view.View;
import android.view.animation.Animation;

public class HideViewAnimationListener extends SimpleAnimationListener {
    private View mView;

    public HideViewAnimationListener(View view) {
        mView = view;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        ViewUtils.hide(mView);
    }
}
