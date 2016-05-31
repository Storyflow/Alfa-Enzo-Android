package com.thirdandloom.storyflow.utils.animations;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.thirdandloom.storyflow.StoryflowApplication;

import android.view.MotionEvent;
import android.view.View;

public class SpringAnimation {
    private static final SpringConfig CONFIG = SpringConfig.fromOrigamiTensionAndFriction(150, 10);
    private static final float END = 0.f;
    private static final float START = 1.f;
    private static final float CALL_CLICK_BARRIER = 0.2f;

    private final View animatedView;
    private final Spring spring;
    private final ViewSpringListener listener = new ViewSpringListener();
    private final boolean visibleAfterClick;

    public static SpringAnimation init(View view) {
        return new SpringAnimation(view, false);
    }

    public static SpringAnimation initVisibleAfterClick(View view) {
        return new SpringAnimation(view, true);
    }


    public SpringAnimation(View view, boolean visible) {
        if (!view.hasOnClickListeners()) {
            throw new UnsupportedOperationException("SpringAnimation does not work without on click listeners");
        }
        animatedView = view;
        visibleAfterClick = visible;
        spring = SpringSystem.create().createSpring();
        spring.setOvershootClampingEnabled(false);
        spring.setSpringConfig(CONFIG);
        spring.addListener(listener);
        init();
    }

    private void init() {
        animatedView.setOnTouchListener((v, event) -> {
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

    public class ViewSpringListener implements SpringListener {

        private boolean started;

        @Override
        public void onSpringUpdate(Spring spring) {
            float value = (float) spring.getCurrentValue();
            float scale = START - (value * 0.3f);
            animatedView.setScaleX(scale);
            animatedView.setScaleY(scale);

            if (!visibleAfterClick && started && value < CALL_CLICK_BARRIER && spring.getEndValue() == END) {
                started = false;
                animatedView.callOnClick();
                spring.setCurrentValue(END);
            }
        }

        @Override
        public void onSpringAtRest(Spring spring) {

        }

        @Override
        public void onSpringActivate(Spring spring) {
            started = true;
        }

        @Override
        public void onSpringEndStateChange(Spring spring) {
            if (spring.getEndValue() == END && visibleAfterClick) {
                animatedView.callOnClick();
            }
        }
    }
}
