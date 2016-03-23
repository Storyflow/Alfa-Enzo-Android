package com.thirdandloom.storyflow.utils.animations;

import com.thirdandloom.storyflow.utils.ViewUtils;

import android.view.View;
import android.view.animation.Animation;

public class ShowViewAnimationListener extends SimpleAnimationListener {
    private View mView;

    public ShowViewAnimationListener(View view) {
        mView = view;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        ViewUtils.show(mView);
    }
}
