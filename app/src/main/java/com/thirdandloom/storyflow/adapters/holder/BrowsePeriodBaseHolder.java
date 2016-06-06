package com.thirdandloom.storyflow.adapters.holder;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.views.LockedNotifyScrollView;
import com.thirdandloom.storyflow.views.OnSwipeStartNotifyRefreshLayout;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

public abstract class BrowsePeriodBaseHolder extends RecyclerView.ViewHolder {

    public interface Actions {
        void onDragStarted();
        void onDragFinished(int velocity);
        void pullToRefreshMotionNotifier(int motionEventAction);
        void onDrag(float scrollAbsolute, float scrollDelta, View scrollingView, Calendar calendar);
        void onClick(View view, Calendar calendar);
        void onPullToRefreshStarted(SwipeRefreshLayout refreshLayout, Calendar calendar, int adapterPosition);
    }

    protected TextView dateTopTextView;
    protected TextView dateBottomTextView;
    protected Actions actions;
    protected OnSwipeStartNotifyRefreshLayout swipeRefreshLayout;
    protected LockedNotifyScrollView notifyScrollView;

    private Calendar periodDate;

    public BrowsePeriodBaseHolder(View itemView, Actions actions) {
        super(itemView);
        this.actions = actions;
        findViews();
        initGui();
    }

    private void initGui() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(R.color.yellow, R.color.grey);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                actions.onPullToRefreshStarted(swipeRefreshLayout, periodDate, BrowsePeriodBaseHolder.this.getAdapterPosition());
            });
            swipeRefreshLayout.setNotifier(actions::pullToRefreshMotionNotifier);
        }
        if (notifyScrollView != null) {
            notifyScrollView.setActions(scrollActions);
        }
    }

    public void setDateRepresentation(String topText, String bottomText) {
        dateTopTextView.setText(topText);
        dateBottomTextView.setText(bottomText);
    }

    public void setPeriodDate(Calendar periodDate) {
        this.periodDate = periodDate;
    }

    protected abstract void findViews();

    private final LockedNotifyScrollView.Actions scrollActions = new LockedNotifyScrollView.Actions() {
        @Override
        public void onDragStarted() {
            actions.onDragStarted();
        }

        @Override
        public void onDragFinished(int velocity) {
            actions.onDragFinished(velocity);
        }

        @Override
        public void onClicked(View view) {
            actions.onClick(view, periodDate);
        }

        @Override
        public void onDrag(float scrollAbsolute, float scrollDelta, View view) {
            actions.onDrag(scrollAbsolute, scrollDelta, view, periodDate);
        }
    };
}
