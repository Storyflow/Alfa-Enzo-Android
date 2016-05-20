package com.thirdandloom.storyflow.adapters;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.views.recyclerview.decoration.StickyHeaderAdapter;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadingStoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyHeaderAdapter<ReadingStoriesAdapter.ReadingStoryHeaderHolder> {

    private static final int STORY = 0;
    private static final int LOADING = 1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        switch (viewType) {
            case STORY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_stories_item, parent, false);
                return new ReadingStoryHolder(view);
            case LOADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_stories_progress, parent, false);
                return new ReadingStoryProgressHolder(view);
            default:
                throw new UnsupportedOperationException("You are using unsupported view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case STORY:
                holder.itemView.setPadding(0, getItemTopPadding(position), 0, 0);
                break;
            case LOADING:
                break;
            default:
                throw new UnsupportedOperationException("You are using unsupported view type");
        }
    }

    @Override
    public int getItemCount() {
        int displayedItemsCount = 50;
        return displayedItemsCount + 1;
    }

    @Override
    public long getHeaderId(int position) {
        return (long) position/3;
    }

    @Override
    public ReadingStoryHeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_stories_header, parent, false);
        return new ReadingStoryHeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(ReadingStoryHeaderHolder viewholder, int position) {
        viewholder.header.setText(getHeaderId(position) + "");
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount() - 1
                ? LOADING
                : STORY;
    }

    private int getItemTopPadding(int position) {
        if (position == 0) {
            return 0;
        }
        return getHeaderId(position) != getHeaderId(position - 1)
                ? StoryflowApplication.resources().getDimensionPixelSize(R.dimen.headerHeight)
                : 0;

    }

    public static class ReadingStoryHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        
        public ReadingStoryHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_imageview);
        }
    }

    public static class ReadingStoryHeaderHolder extends RecyclerView.ViewHolder {
        public TextView header;

        public ReadingStoryHeaderHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView;
        }
    }

    public static class ReadingStoryProgressHolder extends RecyclerView.ViewHolder {
        public ReadingStoryProgressHolder(View itemView) {
            super(itemView);
        }
    }
}
