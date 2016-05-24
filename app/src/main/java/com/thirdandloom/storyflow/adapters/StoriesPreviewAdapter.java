package com.thirdandloom.storyflow.adapters;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.MathUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

public class StoriesPreviewAdapter extends RecyclerView.Adapter<StoriesPreviewAdapter.StoryContentHolder> {

    enum DataType {
        EmptyStories, PendingStories, PopulatedStories
    }

    private Context context;
    private Story.WrapList wrapStoriesList;
    private int itemWidthPixels;
    private DataType dataType;
    private Date currentDate;

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
        holder.configureUi(story, context, itemWidthPixels);
    }

    @Override
    public int getItemCount() {
        return dataType != DataType.PopulatedStories
                ? 0
                : wrapStoriesList.getStories().size();
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void deleteStory(PendingStory pendingStory) {
        ArrayUtils.forEach(wrapStoriesList.getStories(), (story, position) -> {
            if (story.getLocalUid().equals(pendingStory.getLocalUid())) {
                wrapStoriesList.removeStory(story);
                notifyItemRemoved(position);
                return false;
            }
            return true;
        });
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

        View pendingActionsContainer;
        View retryButton;
        View deleteButton;
        String storyLocalUid;

        public StoryContentHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_text_view);
            imageView = (ImageView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_image_view);
            pendingActionsContainer = itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_pending_container);
            retryButton = itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_pending_retry);
            deleteButton = itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_pending_delete);

            retryButton.setOnClickListener(v -> {
                StoryflowApplication.getPendingStoriesManager().retry(storyLocalUid);
                ViewUtils.hide(pendingActionsContainer);
            });
            deleteButton.setOnClickListener(v -> {
                StoryflowApplication.getPendingStoriesManager().remove(storyLocalUid);
                ViewUtils.hide(pendingActionsContainer);
            });
        }

        public void configureUi(Story story, Context context, int itemWidthPixels) {
            textView.setText(story.getDescription());
            storyLocalUid = story.getLocalUid();

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
                        imageHeight = AndroidUtils.dp(100);
                        imageWidth = AndroidUtils.dp(100);
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
                        imageHeight = AndroidUtils.dp(100);
                        imageWidth = AndroidUtils.dp(100);
                        scaleType = ImageView.ScaleType.CENTER_CROP;
                    }

                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported story type.");
            }

            float coef = MathUtils.calculateMaxScaleRatio(imageWidth, imageHeight, itemWidthPixels);
            height = Math.round(coef * imageHeight);

            configureImage(context, imageUrl, height, scaleType);
            configurePendingActions(story.getPendingStatus());
        }

        private void configureImage(Context context, String url, int height, ImageView.ScaleType scaleType) {
            //TODO
            //scaleType be removed after story.getAuthor().getCroppedImageCover().getRect()
            //fixed: -180x106x735x391
            imageView.setScaleType(scaleType);
            ViewUtils.applyHeight(itemView, height);
            Glide
                    .with(context)
                    .load(url)
                    .crossFade()
                    .into(imageView);
        }

        private void configurePendingActions(PendingStory.Status pendingStatus) {
            switch (pendingStatus) {
                case WaitingForSend:
                case OnServer:
                case CreateSucceed:
                case CreatingStory:
                case ImageUploading:
                    ViewUtils.hide(pendingActionsContainer);
                    break;
                case CreateFailed:
                    ViewUtils.show(pendingActionsContainer);
                    ViewUtils.show(retryButton);
                    ViewUtils.show(deleteButton);
                    break;
                case CreateImpossible:
                    ViewUtils.show(pendingActionsContainer);
                    ViewUtils.show(deleteButton);
                    break;
                default:
                    break;

            }
        }
    }
}
