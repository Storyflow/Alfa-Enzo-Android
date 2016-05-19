package com.thirdandloom.storyflow.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class ReadingStoriesAdapter extends RecyclerView.Adapter<ReadingStoriesAdapter.ReadingStoryHolder> {

    @Override
    public ReadingStoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ReadingStoryHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ReadingStoryHolder extends RecyclerView.ViewHolder {

        public ReadingStoryHolder(View itemView) {
            super(itemView);
        }
    }
}
