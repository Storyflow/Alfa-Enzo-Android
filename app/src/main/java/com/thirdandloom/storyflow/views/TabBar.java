package com.thirdandloom.storyflow.views;

import com.facebook.rebound.Spring;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.animations.SpringAnimation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.List;

public class TabBar extends LinearLayout {
    public interface Actions {
        void updatesClicked();
        void messagesClicked();
        void postClicked();
        void profileClicked();
        void homeClicked();
        void homeLongClicked();
        Window getWindow();
    }

    private int scrollPosition;
    private int itemWidth;
    private View flipCircleView;
    private OnScrollListener recyclerViewScrollListener = new OnScrollListener();
    private Actions actions;
    private View triangleView;
    private ViewGroup circlesContainer;
    private View flipCircleContainerView;

    public TabBar(Context context) {
        this(context, null);
    }

    public TabBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_tab_bar, this);
        setOrientation(HORIZONTAL);
        setMinimumHeight(StoryflowApplication.resources().getDimensionPixelOffset(R.dimen.tabBarHeight));
        setPadding(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.sizeNormal));

        View updatesView = findViewById(R.id.view_tab_bar_updates);
        View messagesView = findViewById(R.id.view_tab_bar_messages);
        View postView = findViewById(R.id.view_tab_bar_post);
        View profileView = findViewById(R.id.view_tab_bar_profile);
        flipCircleContainerView = findViewById(R.id.view_tab_bar_flip_ircles_container);

        updatesView.setOnClickListener(v -> actions.updatesClicked());
        messagesView.setOnClickListener(v -> actions.messagesClicked());
        postView.setOnClickListener(v -> actions.postClicked());
        profileView.setOnClickListener(v -> actions.profileClicked());

        SpringAnimation.init(postView);
        SpringAnimation.init(profileView);
        SpringAnimation.init(messagesView);
        SpringAnimation.init(updatesView);

        flipCircleView = findViewById(R.id.view_tab_bar_flippable_circle);
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        flipCircleView.setCameraDistance(scale);

        triangleView = findViewById(R.id.view_tab_bar_triangle_image_view);
        circlesContainer = (ViewGroup)findViewById(R.id.view_tab_bar_circles_container);
        circlesContainer.setOnClickListener(v -> {
            actions.homeClicked();
        });
        circlesContainer.setOnLongClickListener(v -> {
            actions.homeLongClicked();
            return true;
        });

        List<View> animatedViews = Arrays.asList(triangleView, flipCircleView);
        SpringAnimationListener springAnimationListener = new SpringAnimationListener(animatedViews, circlesContainer, true);
        SpringAnimation.init(circlesContainer, springAnimationListener, new OnTouchCirclesListener());
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
        this.scrollPosition = 0;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public OnScrollListener getRecyclerViewScrollListener() {
        return recyclerViewScrollListener;
    }

    private class OnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            scrollPosition += dx;

            int integrate = (int)Math.floor(scrollPosition/itemWidth);
            float fraction = (float)scrollPosition/itemWidth - integrate;
            float angle = fraction * 100 * 1.8f;
            flipCircleView.setRotationY(angle);
        }
    }

    public class OnTouchCirclesListener extends SpringAnimation.ClickableOnTouchListener {

        @Override
        protected boolean onTouchView(View v, MotionEvent event) {
            return super.onTouchView(v, event);
            //switch (event.getAction()) {
            //    case MotionEvent.ACTION_DOWN:
            //        break;
            //    case MotionEvent.ACTION_MOVE:
            //        Timber.d("onTouchView action:%d, rawX: %.2f rawY: %.2f x:%.2f y:%.2f", event.getAction(), event.getRawX(), event.getRawY(), event.getX(), event.getY());
            //        listener.breakAnyClick = true;
            //
            //
            //        Window window = actions.getWindow();
            //        if (circlesContainer.indexOfChild(flipCircleContainerView) >= 0) {
            //            circlesContainer.removeView(flipCircleContainerView);
            //            Timber.d("onTouchView removeFromParent flipCircleContainerView");
            //        }
            //        //start mooving
            //        break;
            //    case MotionEvent.ACTION_UP:
            //        if (circlesContainer.indexOfChild(flipCircleContainerView) < 0) {
            //            circlesContainer.addView(flipCircleContainerView);
            //            Timber.d("onTouchView circlesContainer addView flipCircleContainerView");
            //        }
            //        //break mooving
            //        break;
            //}
            //return true;
        }
    }

    public class SpringAnimationListener extends SpringAnimation.ViewSpringListener {

        private boolean performLongClick;

        public SpringAnimationListener(List<View> animatedViewsList, View clickableView, boolean visibleAfterClick) {
            super(animatedViewsList, clickableView, visibleAfterClick);
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            super.onSpringUpdate(spring);
            if ((userAction == MotionEvent.ACTION_DOWN || userAction == MotionEvent.ACTION_MOVE)
                    && (float) spring.getCurrentValue() >= SpringAnimation.START) {
                if (spring.getEndValue() == SpringAnimation.START) {
                    performLongClick = true;
                    spring.setEndValue(SpringAnimation.END);
                }
            }
        }

        @Override
        protected float getScaleValue(Spring spring) {
            float value = (float) spring.getCurrentValue();
            float scale = performLongClick
                                ? SpringAnimation.START - (value * 1.0f)
                                : SpringAnimation.START + (value * 0.5f);
            return scale;
        }

        @Override
        public void onSpringAtRest(Spring spring) {
            super.onSpringAtRest(spring);

            if (performLongClick && (userAction == MotionEvent.ACTION_DOWN || userAction == MotionEvent.ACTION_MOVE)) {
                performLongClick = false;
                if (!breakAnyClick) clickableView.performLongClick();
            }
        }

        @Override
        public void onSpringEndStateChange(Spring spring) {
            if (spring.getEndValue() == SpringAnimation.END && !performLongClick) {
                clickableView.callOnClick();
            }
        }

        @Override
        protected void reset() {
            super.reset();
            performLongClick = false;
        }
    }
}
