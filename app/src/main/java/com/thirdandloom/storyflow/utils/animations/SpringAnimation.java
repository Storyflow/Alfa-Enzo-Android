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
    private ClickableOnTouchListener onTouchListener;

    public static SpringAnimation init(View view) {
        return new SpringAnimation(view, false);
    }

    public static SpringAnimation initVisibleAfterClick(View view) {
        return new SpringAnimation(view, true);
    }

    public static SpringAnimation init(View clickableView, ViewSpringListener listener, ClickableOnTouchListener onTouchListener) {
        return new SpringAnimation(clickableView, listener, onTouchListener);
    }

    public SpringAnimation(View view, boolean visible) {
        this(Arrays.asList(view), view, visible);
    }

    public SpringAnimation(View clickableView, ViewSpringListener listener, ClickableOnTouchListener onTouchListener) {
        if (!clickableView.hasOnClickListeners()) {
            throw new UnsupportedOperationException("SpringAnimation does not work without on click listeners");
        }
        initSpring(clickableView);
        initListener(listener, onTouchListener);
    }

    public SpringAnimation(List<View> animatedViews, View clickableView, boolean visible) {
        if (!clickableView.hasOnClickListeners()) {
            throw new UnsupportedOperationException("SpringAnimation does not work without on click listeners");
        }
        initSpring(clickableView);
        ViewSpringListener viewSpringListener = new ViewSpringListener(animatedViews, clickableView, visible);
        ClickableOnTouchListener touchListener = new ClickableOnTouchListener();
        initListener(viewSpringListener, touchListener);
    }

    private void initSpring(View clickableView) {
        this.clickableView = clickableView;
        spring = SpringSystem.create().createSpring();
        spring.setOvershootClampingEnabled(false);
        spring.setSpringConfig(CONFIG);
    }

    private void initListener(ViewSpringListener listener, ClickableOnTouchListener onTouchListener) {
        this.spring.addListener(listener);
        this.listener = listener;
        this.onTouchListener = onTouchListener;
        this.onTouchListener.setListener(listener);
        this.onTouchListener.setSpring(spring);
        clickableView.setOnTouchListener(onTouchListener);
    }

    public static class ClickableOnTouchListener implements View.OnTouchListener {

        protected ViewSpringListener listener;
        protected Spring spring;

        public void setListener(ViewSpringListener listener) {
            this.listener = listener;
        }

        public void setSpring(Spring spring) {
            this.spring = spring;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            listener.setUserAction(event.getAction());
            return onTouchView(v, event);
        }

        protected boolean onTouchView(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    listener.started = false;
                    listener.breakAnyClick = false;
                    spring.setEndValue(START);
                    break;
                case MotionEvent.ACTION_UP:
                    if (!listener.started) {
                        StoryflowApplication.runOnUIThread(() -> spring.setEndValue(END), 100);
                    } else {
                        spring.setEndValue(END);
                    }
                    listener.reset();
                    break;
            }
            return true;
        }
    }

    public static class ViewSpringListener implements SpringListener {

        protected final List<View> animatedViewsList;
        protected int userAction;
        protected final View clickableView;
        private final boolean visibleAfterClick;

        public boolean started;
        public boolean breakAnyClick;

        public ViewSpringListener(List<View> animatedViewsList, View clickableView, boolean visibleAfterClick) {
            this.animatedViewsList = animatedViewsList;
            this.clickableView = clickableView;
            this.visibleAfterClick = visibleAfterClick;
        }

        public void setUserAction(int userAction) {
            this.userAction = userAction;
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
                if (!breakAnyClick) clickableView.callOnClick();
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
                if (!breakAnyClick) clickableView.callOnClick();
            }
        }

        protected float getScaleValue(Spring spring) {
            float value = (float) spring.getCurrentValue();
            float scale = START - (value * 0.3f);
            return scale;
        }

        protected void reset() {

        }
    }
}
