package com.thirdandloom.storyflow.adapters;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.MathUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import rx.functions.Action4;

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
    private int itemWidthPixels;
    private DataType dataType;

    public StoriesPreviewAdapter(Context context, @Nullable Story.WrapList wrapStoriesList, int itemWidthPixels) {
        this.context = context;
        setData(wrapStoriesList, itemWidthPixels);
    }

    public void setData(@Nullable Story.WrapList wrapStoriesList, int itemWidthPixels) {
        this.wrapStoriesList = wrapStoriesList;
        this.itemWidthPixels = itemWidthPixels;
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
        getStoryData(story, (height, description, url, scaleType) -> {
            //TODO
            //scaleType be removed after story.getAuthor().getCroppedImageCover().getRect()
            //fixed: -180x106x735x391
            holder.imageView.setScaleType(scaleType);

            ViewUtils.applyHeight(holder.itemView, height);
            Glide
                    .with(context)
                    .load(url)
                    .crossFade()
                    .into(holder.imageView);
            holder.textView.setText(description);
        });
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

    private void getStoryData(Story story, Action4<Integer, String, String, ImageView.ScaleType> dataCallback) {
        String description = story.getDescription();
        String imageUrl;
        int height;
        int imageHeight;
        int imageWidth;
        ImageView.ScaleType scaleType;
        switch (story.getType()) {
            case Text:
                imageUrl = story.getAuthor().getCroppedImageCover().getImageUrl();
                if (story.getAuthor().getCroppedImageCover().getRect() != null
                        && story.getAuthor().getCroppedImageCover().getRect().height() != 0
                        && story.getAuthor().getCroppedImageCover().getRect().width() != 0) {
                    imageHeight = story.getAuthor().getCroppedImageCover().getRect().height();
                    imageWidth = story.getAuthor().getCroppedImageCover().getRect().width();
                } else {
                    imageHeight = DeviceUtils.dp(100);
                    imageWidth = DeviceUtils.dp(100);
                }
                scaleType = ImageView.ScaleType.CENTER_CROP;
                break;
            case Image:
                imageUrl = story.getImageData().getNormalSizedImage().url();
                imageHeight = story.getImageData().getNormalSizedImage().size().height();
                imageWidth = story.getImageData().getNormalSizedImage().size().width();
                scaleType = ImageView.ScaleType.FIT_CENTER;

                //TODO
                //this code should be removed after story.getImageData().getNormalSizedImage().size()
                //fixed: story.getImageData().getNormalSizedImage().size() = (0, 0)
                if (imageHeight == 0 || imageWidth == 0) {
                    imageHeight = DeviceUtils.dp(100);
                    imageWidth = DeviceUtils.dp(100);
                    scaleType = ImageView.ScaleType.CENTER_CROP;
                }

                break;
            default:
                throw new UnsupportedOperationException("Unsupported story type.");
        }

        float coef = MathUtils.calculateMaxScaleRatio(imageWidth, imageHeight, itemWidthPixels);
        height = Math.round(coef*imageHeight);
        dataCallback.call(height, description, imageUrl, scaleType);
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
