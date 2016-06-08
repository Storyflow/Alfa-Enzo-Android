package com.thirdandloom.storyflow.adapters.holder;

import com.thirdandloom.storyflow.models.Story;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class ReadStoriesBaseViewHolder extends RecyclerView.ViewHolder {

    public ReadStoriesBaseViewHolder(View itemView) {
        super(itemView);
        init();
    }

    protected abstract void init();

    public void initGui(Context context, Story story) {

    }
}
