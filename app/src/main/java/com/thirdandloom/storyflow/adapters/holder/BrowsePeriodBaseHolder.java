package com.thirdandloom.storyflow.adapters.holder;

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

    public BrowsePeriodBaseHolder(View itemView, Actions actions) {
        super(itemView);
        findViews();
    }

    public void setDateRepresentation(String topText, String bottomText) {
        dateTopTextView.setText(topText);
        dateBottomTextView.setText(bottomText);
    }

    protected abstract void findViews();
}
