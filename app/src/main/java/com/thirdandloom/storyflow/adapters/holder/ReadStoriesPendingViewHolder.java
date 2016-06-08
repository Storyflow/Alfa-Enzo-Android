package com.thirdandloom.storyflow.adapters.holder;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.models.Story;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReadStoriesPendingViewHolder extends ReadStoriesBaseViewHolder {

    public static ReadStoriesPendingViewHolder newInstance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_stories_progress, parent, false);
        return new ReadStoriesPendingViewHolder(itemView);
    }

    public ReadStoriesPendingViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void init() {

    }
}
