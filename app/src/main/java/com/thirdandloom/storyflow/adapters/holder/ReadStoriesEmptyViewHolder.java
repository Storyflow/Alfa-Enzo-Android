package com.thirdandloom.storyflow.adapters.holder;

import com.thirdandloom.storyflow.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReadStoriesEmptyViewHolder extends ReadStoriesBaseViewHolder {

    public static ReadStoriesEmptyViewHolder newInstance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_empty_stories, parent, false);
        return new ReadStoriesEmptyViewHolder(itemView);
    }

    public ReadStoriesEmptyViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void init() {

    }
}
