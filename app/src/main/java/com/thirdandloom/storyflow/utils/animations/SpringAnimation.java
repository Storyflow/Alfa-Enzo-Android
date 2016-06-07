package com.thirdandloom.storyflow.utils.animations;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.thirdandloom.storyflow.StoryflowApplication;

import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.List;

public class SpringAnimation {
    public static final SpringConfig CONFIG = SpringConfig.fromOrigamiTensionAndFriction(150, 10);
    public static final float END = 0.f;
    public static final float START = 1.f;
    public static final float CALL_CLICK_BARRIER = 0.2f;

    private View clickableView;
    private ViewSpringListener listener;
    private Spring spring;

    public static SpringAnimation init(View view) {
        return new SpringAnimation(view, false);
    }

    public static SpringAnimation initVisibleAfterClick(View view) {
        return new SpringAnimation(view, true);
    }

    public static SpringAnimation init(View clickableView, ViewSpringListener listener) {
        return new SpringAnimation(clickableView, listener);
    }

    public SpringAnimation(View view, boolean visible) {
        this(Arrays.asList(view), view, visible);
    }

    public SpringAnimation(View clickableView, ViewSpringListener listener) {
        if (!clickableView.hasOnClickListeners()) {
            throw new UnsupportedOperationException("SpringAnimation does not work without on click listeners");
        }
        initSpring(clickableView);
        initListener(listener);
    }

    public SpringAnimation(List<View> animatedViews, View clickableView, boolean visible) {
        if (!clickableView.hasOnClickListeners()) {
            throw new UnsupportedOperationException("SpringAnimation does not work without on click listeners");
        }
        initSpring(clickableView);
        initListener(new ViewSpringListener(animatedViews, clickableView, visible));
    }

    private void initSpring(View clickableView) {
        this.clickableView = clickableView;
        spring = SpringSystem.create().createSpring();
        spring.setOvershootClampingEnabled(false);
        spring.setSpringConfig(CONFIG);
    }

    private void initListener(ViewSpringListener listener) {
        this.spring.addListener(listener);
        this.listener = listener;
        initDefaultOnTouch();
    }

    private void initDefaultOnTouch() {
        clickableView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    listener.started = false;
                    spring.setEndValue(START);
                    break;
                case MotionEvent.ACTION_UP:
                    if (!listener.started) {
                        StoryflowApplication.runOnUIThread(() -> spring.setEndValue(END), 100);
                    } else {
                        spring.setEndValue(END);
                    }
                    break;
            }
            return true;
        });
    }

    public static class ViewSpringListener implements SpringListener {

        protected final List<View> animatedViewsList;
        private final View clickableView;
        private final boolean visibleAfterClick;

        public boolean started;

        public ViewSpringListener(List<View> animatedViewsList, View clickableView, boolean visibleAfterClick) {
            this.animatedViewsList = animatedViewsList;
            this.clickableView = clickableView;
            this.visibleAfterClick = visibleAfterClick;
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            float scale = getScaleValue(spring);

            for (View animatedView : animatedViewsList) {
                animatedView.setScaleX(scale);
                animatedView.setScaleY(scale);
            }

            if (!visibleAfterClick && started && (float) spring.getCurrentValue() < CALL_CLICK_BARRIER && spring.getEndValue() == END) {
                started = false;
                clickableView.callOnClick();
                spring.setCurrentValue(END);
            }
        }

        @Override
        public void onSpringAtRest(Spring spring) {}

        @Override
        public void onSpringActivate(Spring spring) {
            started = true;
        }

        @Override
        public void onSpringEndStateChange(Spring spring) {
            if (spring.getEndValue() == END && visibleAfterClick) {
                clickableView.callOnClick();
            }
        }

        protected float getScaleValue(Spring spring) {
            float value = (float) spring.getCurrentValue();
            float scale = START - (value * 0.3f);
            return scale;
        }
    }
}
