package com.thirdandloom.storyflow.adapters.holder;

import com.thirdandloom.storyflow.models.Story;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class ReadStoriesBaseViewHolder extends RecyclerView.ViewHolder {

    protected Context context;
    protected Story story;

    public ReadStoriesBaseViewHolder(View itemView) {
        super(itemView);
        init();
    }

    protected abstract void init();

    public void initGui(Context context, Story story) {
        this.context = context;
        this.story = story;
    }

    public int getPostLayoutContainerHeight() {
        return 0;
    }

    public int getPreLayoutContainerHeight() {
        return 0;
    }

    public Story getStory() {
        return story;
    }
}
