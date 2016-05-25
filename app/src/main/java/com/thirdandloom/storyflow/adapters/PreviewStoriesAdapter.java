package com.thirdandloom.storyflow.adapters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.models.Story;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

public class PreviewStoriesAdapter extends RecyclerView.Adapter<PreviewStoriesAdapter.PreviewStoryHolder> {

    private List<Story> stories = new LinkedList<>();
    private Context context;

    public PreviewStoriesAdapter(Context context, Story initialStory) {
        this.context = context;
        this.stories.add(initialStory);
    }

    public void addStories(List<Story> addedStories, int start) {
        stories.addAll(start, addedStories);
        notifyItemRangeInserted(start, addedStories.size());
    }

    @Override
    public PreviewStoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_preview_story, parent, false);
        PreviewStoryHolder previewStoryHolder = new PreviewStoryHolder(v);
        return previewStoryHolder;
    }

    @Override
    public void onBindViewHolder(PreviewStoryHolder holder, int position) {
        holder.setData(context, stories.get(position));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public static class PreviewStoryHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public PreviewStoryHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.adapter_recycler_item_preview_story_image_view);
        }

        public void setData(Context context, Story story) {
            switch (story.getType()) {
                case Image:
                    String imageUrl = story.getImageData().getNormalSizedImage().url();
                    Glide
                            .with(context)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .crossFade()
                            .into(imageView);
                    break;
            }
        }
    }
}
