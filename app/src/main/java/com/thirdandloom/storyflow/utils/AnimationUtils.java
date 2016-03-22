package com.thirdandloom.storyflow.utils;

import android.animation.Animator;
import android.view.View;

public class AnimationUtils {
    public static void applyStartAnimation(View contentView, View circleView) {
        ViewUtils.callOnPreDraw(circleView, view -> {
            startRevealCircleAnimation(circleView, contentView);
        });
    }

    private static void startRevealCircleAnimation(View circleView, View launchView) {
        circleView.setLayerType(View.LAYER_TYPE_NONE, null);
        int newRadius = DeviceUtils.getLargerDisplaySize();
        float delta = newRadius/circleView.getHeight();
        circleView.animate()
                .scaleXBy(delta)
                .scaleYBy(delta)
                .setDuration(300)
                .setListener(new AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        circleView.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                })
                .start();
        launchView.animate()
                .alphaBy(-1.f)
                .setDuration(200)
                .setStartDelay(100)
                .setListener(new AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewUtils.hide(launchView);
                    }
                }).start();

    }
}
