package com.thirdandloom.storyflow.views;

import com.facebook.rebound.Spring;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.animations.SpringAnimation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
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
    }

    private int scrollPosition;
    private int itemWidth;
    private View flipCircleView;
    private OnScrollListener recyclerViewScrollListener = new OnScrollListener();
    private Actions actions;
    private View triangleView;
    private View circlesContainer;

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
        circlesContainer = findViewById(R.id.view_tab_bar_circles_container);
        circlesContainer.setOnClickListener(v -> {
            actions.homeClicked();
        });

        List<View> animatedViews = Arrays.asList(triangleView, flipCircleView);
        SpringAnimationListener springAnimationListener = new SpringAnimationListener(animatedViews, circlesContainer, true);
        SpringAnimation.init(circlesContainer, springAnimationListener);
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

    public class SpringAnimationListener extends SpringAnimation.ViewSpringListener {

        public SpringAnimationListener(List<View> animatedViewsList, View clickableView, boolean visibleAfterClick) {
            super(animatedViewsList, clickableView, visibleAfterClick);
        }

        @Override
        protected float getScaleValue(Spring spring) {
            float value = (float) spring.getCurrentValue();
            float scale = SpringAnimation.START + (value * 0.5f);
            return scale;
        }
    }
}
