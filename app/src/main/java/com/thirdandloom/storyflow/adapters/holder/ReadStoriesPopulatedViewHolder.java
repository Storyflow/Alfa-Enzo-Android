package com.thirdandloom.storyflow.adapters.holder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.Author;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.MathUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.glide.CropCircleTransformation;
import com.thirdandloom.storyflow.utils.image.StoryflowImageUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadStoriesPopulatedViewHolder extends ReadStoriesBaseViewHolder {

    public ImageView imageView;

    private TextView descriptionTextView;
    private View pendingActionsContainer;
    private View retryButton;
    private View deleteButton;
    private String storyLocalUid;

    private ImageView authorAvatarImageView;
    private TextView authorFullNameTextView;
    private TextView authorUserNameTextView;
    private View storyImageContainer;

    public static ReadStoriesPopulatedViewHolder newInstance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_stories_item, parent, false);
        return new ReadStoriesPopulatedViewHolder(itemView);
    }

    public ReadStoriesPopulatedViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void init() {
        imageView = (ImageView)itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_imageview);
        descriptionTextView = (TextView)itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_description);
        pendingActionsContainer = itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_pending_container);
        retryButton = itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_pending_retry);
        deleteButton = itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_pending_delete);
        authorAvatarImageView = (ImageView)itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_avatar_imageview);
        authorFullNameTextView = (TextView)itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_full_name_textview);
        authorUserNameTextView = (TextView)itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_user_name_textview);
        storyImageContainer = itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_story_image_container);

        retryButton.setOnClickListener(v -> {
            StoryflowApplication.getPendingStoriesManager().retry(storyLocalUid);
            ViewUtils.hide(pendingActionsContainer);
        });
        deleteButton.setOnClickListener(v -> {
            StoryflowApplication.getPendingStoriesManager().remove(storyLocalUid);
            ViewUtils.hide(pendingActionsContainer);
        });
    }

    @Override
    public void initGui(Context context, Story story) {
        super.initGui(context, story);

        storyLocalUid = story.getLocalUid();
        descriptionTextView.setText(story.getDescription());
        configureAuthor(story.getAuthor(), context);
        configurePendingActions(story.getPendingStatus());
        switch (story.getType()) {
            case Text:
                StoryflowImageUtils.Config.with(context, imageView)
                        .itemWidthPx(DeviceUtils.getDisplayWidth())
                        .story(story)
                        .showText();
                break;
            case Image:
                StoryflowImageUtils.Config.with(context, imageView)
                        .itemWidthPx(DeviceUtils.getDisplayWidth())
                        .story(story)
                        .showImage();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported story type.");
        }
    }

    private void configureAuthor(Author author, Context context) {
        authorUserNameTextView.setText(author.getUserName());
        authorFullNameTextView.setText(author.getFullName());
        Glide
                .with(context)
                .load(author.getCroppedAvatar().getImageUrl())
                .bitmapTransform(new CropCircleTransformation(context))
                .dontAnimate()
                .into(authorAvatarImageView);
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
