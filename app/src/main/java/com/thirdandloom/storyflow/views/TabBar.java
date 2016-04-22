package com.thirdandloom.storyflow.views;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.utils.DeviceUtils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class TabBar extends LinearLayout {
    private static final int MIN_HEIGHT_DP = 68;

    public interface Actions {
        void updatesClicked();
        void messagesClicked();
        void postClicked();
        void profileClicked();
    }

    private int scrollPosition;
    private int itemWidth;
    private View flipCircleView;
    private OnScrollListener recyclerViewScrollListener = new OnScrollListener();
    private Actions actions;

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
        setMinimumHeight(DeviceUtils.dpToPx(MIN_HEIGHT_DP));
        setBackground(getResources().getDrawable(R.drawable.shape_black_gradient));
        setPadding(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.sizeNormal));

        findViewById(R.id.view_tab_bar_updates).setOnClickListener(v -> {
            actions.updatesClicked();
        });
        findViewById(R.id.view_tab_bar_messages).setOnClickListener(v -> {
            actions.messagesClicked();
        });
        findViewById(R.id.view_tab_bar_post).setOnClickListener(v -> {
            actions.postClicked();
        });
        findViewById(R.id.view_tab_bar_profile).setOnClickListener(v -> {
            actions.profileClicked();
        });
        flipCircleView = findViewById(R.id.view_tab_bar_flippable_circle);

        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        flipCircleView.setCameraDistance(scale);
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
}
