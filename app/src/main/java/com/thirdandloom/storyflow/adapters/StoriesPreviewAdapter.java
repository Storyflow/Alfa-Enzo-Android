package com.thirdandloom.storyflow.adapters;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class StoriesPreviewAdapter extends RecyclerView.Adapter<StoriesPreviewAdapter.StoryContentHolder> {

    enum DataType {
        EmptyStories, PendingStories, PopulatedStories
    }

    private Context context;
    private Story.WrapList wrapStoriesList;
    private DataType dataType;

    public StoriesPreviewAdapter(Context context, @Nullable Story.WrapList wrapStoriesList) {
        this.context = context;
        this.wrapStoriesList = wrapStoriesList;
        updateDataType();
    }

    @Override
    public StoryContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_story_content, parent, false);
        StoryContentHolder holder = new StoryContentHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(StoryContentHolder holder, int position) {
        Story story = wrapStoriesList.getStory(position);

        String imageUrl = null;
        String text = story.getDescription();
        int height = 0;

        switch (story.getType()) {
            case Text:
                imageUrl = story.getAuthor().getCroppedImageCover().getImageUrl();
                height = story.getAuthor().getCroppedImageCover().getRect() != null
                        ? story.getAuthor().getCroppedImageCover().getRect().height()
                        : DeviceUtils.dpToPx(200);
                break;
            case Image:
                imageUrl = story.getImageData().getNormalSizedImage().url();
                height = story.getImageData().getNormalSizedImage().size().height();
                break;
        }
        ViewUtils.applyHeight(holder.itemView, height);
        Glide
                .with(context)
                .load(imageUrl)
                .crossFade()
                .into(holder.imageView);
        holder.textView.setText(text);
    }

    @Override
    public int getItemCount() {
        return dataType != DataType.PopulatedStories
                ? 0
                : wrapStoriesList.getStories().size();
    }

    public DataType getDataType() {
        return dataType;
    }

    private void updateDataType() {
        if (this.wrapStoriesList == null) {
            dataType = DataType.PendingStories;
        } else if (this.wrapStoriesList.getStories() == null
                    || this.wrapStoriesList.getStories().size() == 0) {
            dataType = DataType.EmptyStories;
        } else {
            dataType = DataType.PopulatedStories;
        }
    }


    public static class StoryContentHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public StoryContentHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_text_view);
            imageView = (ImageView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_image_view);
        }
    }
}
