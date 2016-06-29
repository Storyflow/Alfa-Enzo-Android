package com.thirdandloom.storyflow.adapters.holder;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.Author;
import com.thirdandloom.storyflow.models.Likes;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.event.ShowWarningEvent;
import com.thirdandloom.storyflow.utils.glide.CropCircleTransformation;
import com.thirdandloom.storyflow.utils.image.StoryflowImageUtils;
import org.greenrobot.eventbus.EventBus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadStoriesPopulatedViewHolder extends ReadStoriesBaseViewHolder {

    public interface Actions {
        Likes onStarClicked(int adapterPosition, ReadStoriesPopulatedViewHolder holder);
        void notifyItemChanged(int adapterPosition);
        void onImageClicked(int adapterPosition, View view);
    }

    public ImageView imageView;

    private TextView descriptionTextView;
    private View pendingActionsContainer;
    private View retryButton;
    private View deleteButton;
    private String storyLocalUid;

    private ImageView authorAvatarImageView;
    private TextView authorFullNameTextView;
    private TextView authorUserNameTextView;
    private View saveButton;
    private View starButton;
    public View storyImageContainer;

    public View starsContainer;
    private ImageView starsFirstAvatar;
    private ImageView starsSecondAvatar;
    private TextView starsTextView;
    private TextView starsCountTextView;
    private TextView commentsCountTextView;
    private boolean containerIsVisible;
    private boolean startViewHolderAnimation;

    private Actions actions;

    public static ReadStoriesPopulatedViewHolder newInstance(ViewGroup parent, Actions actions) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_stories_item, parent, false);
        return createHolder(itemView, actions);
    }

    public static ReadStoriesPopulatedViewHolder newInstanceWithHeader(ViewGroup parent, Actions actions) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_stories_with_header_item, parent, false);
        return createHolder(itemView, actions);
    }

    private static ReadStoriesPopulatedViewHolder createHolder(View itemView, Actions actions) {
        ReadStoriesPopulatedViewHolder holder = new ReadStoriesPopulatedViewHolder(itemView);
        holder.actions = actions;
        return holder;
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
        saveButton = itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_save);
        commentsCountTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_reading_stories_comments_count_text_view);
        starsCountTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_reading_stories_stars_count_text_view);

        starsContainer = itemView.findViewById(R.id.adapter_recycler_item_reading_stories_stars_container);
        starsFirstAvatar = (ImageView) itemView.findViewById(R.id.adapter_recycler_item_reading_stories_stars_ava1);
        starsSecondAvatar = (ImageView) itemView.findViewById(R.id.adapter_recycler_item_reading_stories_stars_ava2);
        starsTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_reading_stories_stars_text_view);
        starButton = itemView.findViewById(R.id.adapter_recycler_item_reading_stories_item_star);

        initListeners();
    }

    private void initListeners() {
        retryButton.setOnClickListener(v -> {
            StoryflowApplication.getPendingStoriesManager().retry(storyLocalUid);
            ViewUtils.hide(pendingActionsContainer);
        });
        deleteButton.setOnClickListener(v -> {
            StoryflowApplication.getPendingStoriesManager().remove(storyLocalUid);
            ViewUtils.hide(pendingActionsContainer);
        });
        saveButton.setOnClickListener(v -> {
            EventBus.getDefault().post(new ShowWarningEvent(R.string.image_will_be_saved_immediately));
            StoryflowImageUtils.saveImage(context, story.getSavedImageUrl());
        });
        starButton.setOnClickListener(v -> {
            onStoryLikesChanged(actions.onStarClicked(getAdapterPosition(), this));
        });
        imageView.setOnClickListener(v -> {
            actions.onImageClicked(getAdapterPosition(), v);
        });
    }

    public void onStoryLikesChanged(Likes storyLikesAfterAction) {
        startViewHolderAnimation = shouldStartAnimateViewHolderChanges(storyLikesAfterAction);
        if (startViewHolderAnimation) {
            actions.notifyItemChanged(getAdapterPosition());
        } else {
            configureLikes();
        }
    }

    @Override
    public int getPostLayoutContainerHeight() {
        return containerIsVisible ? AndroidUtils.dp(44) : 0;
    }

    @Override
    public int getPreLayoutContainerHeight() {
        return containerIsVisible ? AndroidUtils.dp(44) : 0;
    }

    @Override
    public void initGui(Context context, Story story) {
        super.initGui(context, story);

        storyLocalUid = story.getLocalUid();
        descriptionTextView.setText(story.getDescription());
        commentsCountTextView.setText(context.getResources().getQuantityString(R.plurals.plurals_dd_comments, story.getCommentsCount(), story.getCommentsCount()));
        configureAuthor(story.getAuthor(), context);
        configurePendingActions(story.getPendingStatus());
        configureLikes();
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

    private void configureLikes() {
        Likes likes = story.getLikes();
        boolean likesAreNull = likes == null;
        int likesCount = !likesAreNull
                            ? likes.getCount()
                            : 0;
        starsCountTextView.setText(context.getResources().getQuantityString(R.plurals.plurals_dd_stars, likesCount, likesCount));

        if (likesAreNull || (!likes.containsCurrentUserLike() && likesCount == 0)) {
            containerIsVisible = false;
            if (!startViewHolderAnimation) resetStarsViews();
            return;
        }
        if (likes.containsCurrentUserLike()) {
            ViewUtils.setHidden(starsSecondAvatar, likesCount <= 1);
            initIncludedCurrentUserLike(likesCount, likes.getLastLikeAuthor());
        } else {
            ViewUtils.hide(starsSecondAvatar);
            initExcludedUserLike(likesCount, likes.getLastLikeAuthor());
        }
        if (!startViewHolderAnimation) ViewUtils.show(starsContainer);
        containerIsVisible = true;
    }

    private void initExcludedUserLike(int likesCount, Author lastLiked) {
        showAvatar(context, starsFirstAvatar, lastLiked.getCroppedAvatar().getImageUrl());
        if (likesCount == 1) {
            starsTextView.setText(lastLiked.getFullName());
        } else {
            starsTextView.setText(context.getString(R.string.ss_and_count_others, lastLiked.getFullName(), likesCount));
        }
    }

    private void initIncludedCurrentUserLike(int likesCount, Author lastLiked) {
        User currentUser = StoryflowApplication.account().getUser();
        String userAvatarUrl = currentUser.getProfileImage().getImageUrl();
        showAvatar(context, starsFirstAvatar, userAvatarUrl);
        if (likesCount <= 1) {
            starsTextView.setText(currentUser.getFullUserName());
        } else {
            showAvatar(context, starsSecondAvatar, lastLiked.getCroppedAvatar().getImageUrl());
            if (likesCount == 2) {
                starsTextView.setText(context.getString(R.string.you_and_ss, lastLiked.getFullName()));
            } else {
                starsTextView.setText(context.getString(R.string.you_comma_ss_and_count_others, lastLiked.getFullName(), likesCount));
            }
        }
    }

    private void resetStarsViews() {
        starsFirstAvatar.setImageDrawable(null);
        starsSecondAvatar.setImageDrawable(null);
        starsTextView.setText("");
        ViewUtils.hide(starsContainer);
    }

    private void configureAuthor(Author author, Context context) {
        authorUserNameTextView.setText(author.getUserName());
        authorFullNameTextView.setText(author.getFullName());
        showAvatar(context, authorAvatarImageView, author.getCroppedAvatar().getImageUrl());
    }

    private void showAvatar(Context context, ImageView imageView, String url) {
        Glide
                .with(context)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(context))
                .dontAnimate()
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


    public void animationFinished() {
        startViewHolderAnimation = false;
    }

    public void animationStarted(int startedHeight) {
        ViewUtils.show(starsContainer);
        starsContainer.getLayoutParams().height = startedHeight;
        starsContainer.requestLayout();
    }

    private boolean shouldStartAnimateViewHolderChanges(Likes afterActionLikes) {
        return afterActionLikes == null || afterActionLikes.getCount() == 0
                || (afterActionLikes.getCount() == 1 && afterActionLikes.containsCurrentUserLike());
    }
}
