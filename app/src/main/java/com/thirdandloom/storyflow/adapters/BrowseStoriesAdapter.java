package com.thirdandloom.storyflow.adapters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.Mention;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.MathUtils;
import com.thirdandloom.storyflow.utils.SpannableUtils;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.glide.CropCircleTransformation;
import com.thirdandloom.storyflow.utils.glide.RoundedCornersTransformation;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

public class BrowseStoriesAdapter extends RecyclerView.Adapter<BrowseStoriesAdapter.StoryContentHolder> {

    enum DataType {
        EmptyStories, PendingStories, PopulatedStories
    }

    enum AuthorViewType {
        Full, DescriptionOnly, None
    }

    private Context context;
    private Story.WrapList wrapStoriesList;
    private int itemWidthPixels;
    private DataType dataType;
    private Date currentDate;
    private AuthorViewType authorViewType = AuthorViewType.None;

    public BrowseStoriesAdapter(Context context, @Nullable Story.WrapList wrapStoriesList, int itemWidthPixels) {
        this.context = context;
        setData(wrapStoriesList, itemWidthPixels);
    }

    public void setAuthorViewType(AuthorViewType authorViewType) {
        this.authorViewType = authorViewType;
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
        holder.initAuthorUi(context, story, authorViewType);
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
        private ImageView imageView;
        private View pendingActionsContainer;
        private View retryButton;
        private View deleteButton;
        private String storyLocalUid;

        private View authorDataContainer;
        private View storyDescriptionContainer;
        private TextView authorFullNameTextView;
        private ImageView authorAvatar;
        private TextView description;

        public StoryContentHolder(View itemView) {
            super(itemView);
            findViews();
            initListeners();
        }

        private void findViews() {
            imageView = (ImageView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_image_view);
            pendingActionsContainer = itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_pending_container);
            retryButton = itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_pending_retry);
            deleteButton = itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_pending_delete);
            storyDescriptionContainer = itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_description_data_container);
            authorDataContainer = itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_author_data_container);
            authorFullNameTextView = (TextView)itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_author_full_name);
            authorAvatar = (ImageView)itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_author_avatar);
            description = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_text_view);
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
        }

        public void configureUi(Story story, Context context, int itemWidthPixels) {
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
                    .bitmapTransform(new RoundedCornersTransformation(context, AndroidUtils.dp(5.f), 0))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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

        public void initAuthorUi(Context context, Story story, AuthorViewType authorViewType) {
            switch (authorViewType) {
                case None:
                    ViewUtils.hide(storyDescriptionContainer);
                    break;
                case DescriptionOnly:
                    ViewUtils.show(storyDescriptionContainer);
                    ViewUtils.hide(authorDataContainer);
                    setStoryDescription(story);
                    break;
                case Full:
                    ViewUtils.show(storyDescriptionContainer);
                    ViewUtils.show(authorDataContainer);
                    setStoryDescription(story);
                    Glide
                            .with(context)
                            .load(story.getAuthor().getCroppedAvatar().getImageUrl())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .bitmapTransform(new CropCircleTransformation(context))
                            .dontAnimate()
                            .into(authorAvatar);
                    authorFullNameTextView.setText(story.getAuthor().getFullName());
                    break;
                default:
                    throw new UnsupportedOperationException("You are using unsupported authorViewType");
            }
        }

        public void setStoryDescription(Story story) {
            String storyDescription = story.getDescription();
            if (ArrayUtils.isEmpty(story.getMentionsList())) {
                description.setText(storyDescription);
            } else {
                for (Mention mention : story.getMentionsList()) {
                    String mentionName = mention.getMentionUserName();
                    storyDescription = storyDescription.replace(mentionName, mention.getFullName());
                }
                SpannableString ss = new SpannableString(storyDescription);
                for (Mention mention : story.getMentionsList()) {
                    SpannableUtils.setOnClick(ss, new MentionClickable(mention),
                            mention.getFullName(), storyDescription);
                }
                description.setText(ss);
                description.setMovementMethod(LinkMovementMethod.getInstance());
                description.setHighlightColor(Color.TRANSPARENT);
            }
        }

        private static class MentionClickable extends ClickableSpan {
            private final Mention mention;

            public MentionClickable(Mention mention) {
                this.mention = mention;
            }

            @Override
            public void onClick(View textView) {
                Timber.d("onClick mention name: %s", mention.getFullName());
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(StoryflowApplication.resources().getColor(R.color.yellow));
                ds.setUnderlineText(false);
            }
        }
    }
}
