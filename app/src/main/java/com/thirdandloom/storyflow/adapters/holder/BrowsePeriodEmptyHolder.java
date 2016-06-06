package com.thirdandloom.storyflow.adapters.holder;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.views.LockedNotifyScrollView;
import com.thirdandloom.storyflow.views.OnSwipeStartNotifyRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BrowsePeriodEmptyHolder extends BrowsePeriodBaseHolder {

    public static BrowsePeriodEmptyHolder newInstance(ViewGroup parent, Actions actions) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_browse_story_content, parent, false);
        return new BrowsePeriodEmptyHolder(itemView, actions);
    }

    public BrowsePeriodEmptyHolder(View itemView, Actions actions) {
        super(itemView, actions);
    }

    @Override
    protected void findViews() {
        dateTopTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_content_date_top_text_view);
        dateBottomTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_content_date_bottom_text_view);
        swipeRefreshLayout = (OnSwipeStartNotifyRefreshLayout)itemView.findViewById(R.id.adapter_recycler_item_browse_story_content_swipe_container);
        notifyScrollView = (LockedNotifyScrollView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_content_locked_scroll_view);
    }

}
