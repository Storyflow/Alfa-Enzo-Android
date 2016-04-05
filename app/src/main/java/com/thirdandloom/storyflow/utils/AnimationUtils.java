package com.thirdandloom.storyflow.utils;

import com.thirdandloom.storyflow.utils.animations.AnimatorListener;
import com.thirdandloom.storyflow.utils.animations.HideFooterAnimation;
import com.thirdandloom.storyflow.utils.animations.HideHeaderAnimation;
import com.thirdandloom.storyflow.utils.animations.ShowFooterAnimation;
import com.thirdandloom.storyflow.utils.animations.ShowHeaderAnimation;

import android.animation.Animator;
import android.view.View;
import android.view.animation.Animation;

public class AnimationUtils extends BaseUtils {
    public static void showHeader(View header, int duration) {
        Animation animation = new ShowHeaderAnimation(header, duration);
        header.startAnimation(animation);
    }

    public static void showHeader(View header) {
        showHeader(header, getDefaultAnimationDuration());
    }

    public static void showFooter(View footer, int duration) {
        Animation animation = new ShowFooterAnimation(footer, duration);
        footer.startAnimation(animation);
    }

    public static void showFooter(View footer) {
        showFooter(footer, getDefaultAnimationDuration());
    }

    public static void hideHeader(View header, int duration) {
        Animation animation = new HideHeaderAnimation(header, duration);
        header.startAnimation(animation);
    }

    public static void hideHeader(View header) {
        hideHeader(header, getDefaultAnimationDuration());
    }

    public static void hideFooter(View footer, int duration) {
        Animation animation = new HideFooterAnimation(footer, duration);
        footer.startAnimation(animation);
    }

    public static void hideFooter(View footer) {
        hideFooter(footer, getDefaultAnimationDuration());
    }

    public static void applyStartAnimation(View contentView, View circleView) {
        ViewUtils.callOnPreDraw(circleView, view -> {
            startRevealCircleAnimation(circleView, contentView);
        });
    }

    private static void startRevealCircleAnimation(View circleView, View contentView) {
        if (circleView.getHeight() == 0) {
            ViewUtils.hide(contentView);
            return;
        }
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
        contentView.animate()
                .alphaBy(-1.f)
                .setDuration(200)
                .setStartDelay(100)
                .setListener(new AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewUtils.hide(contentView);
                    }
                }).start();
    }
    private static int getDefaultAnimationDuration() {
        return 300;
    }

}
