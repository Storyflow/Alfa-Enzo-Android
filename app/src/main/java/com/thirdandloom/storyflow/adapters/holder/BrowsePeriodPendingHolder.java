package com.thirdandloom.storyflow.adapters.holder;

import com.thirdandloom.storyflow.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BrowsePeriodPendingHolder extends BrowsePeriodEmptyHolder {

    public static BrowsePeriodPendingHolder newInstance(ViewGroup parent, Actions actions) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_browse_story_pending_content, parent, false);
        return new BrowsePeriodPendingHolder(itemView, actions);
    }

    public BrowsePeriodPendingHolder(View itemView, Actions actions) {
        super(itemView, actions);
    }

    @Override
    protected void findViews() {
        dateTopTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_pendig_content_date_top_text_view);
        dateBottomTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_pending_content_date_bottom_text_view);
    }
}
