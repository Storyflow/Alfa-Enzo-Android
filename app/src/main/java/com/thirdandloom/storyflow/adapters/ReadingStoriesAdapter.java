package com.thirdandloom.storyflow.adapters;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.managers.StoriesManager.RequestData;
import com.thirdandloom.storyflow.models.Author;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.DateUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.MathUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.glide.CropCircleTransformation;
import com.thirdandloom.storyflow.utils.models.Time;
import com.thirdandloom.storyflow.views.recyclerview.decoration.StickyHeaderAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ReadingStoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyHeaderAdapter<ReadingStoriesAdapter.ReadingStoryHeaderHolder> {

    private static final int FILLED_STORY = 0;
    private static final int EMPTY_STORY = FILLED_STORY + 1;
    private static final int LOADING = EMPTY_STORY + 1;

    private LinkedList<Story> storiesList = new LinkedList<>();
    private ArrayList<Story> displayedPendingStories = new ArrayList<>();

    private String nextStoryDate;
    private final int limit;
    private final RequestData.Period.Type period;
    private final Context context;

    public ReadingStoriesAdapter(Story.WrapList stories, Calendar dateCalendar, RequestData requestData, Context context) {
        this.limit = requestData.getLimit();
        this.period = requestData.getPeriodType();
        this.context = context;
        addNewStories(stories, dateCalendar);
    }

    public Story getStory(int position) {
        return storiesList.get(position);
    }

    public void addNewStories(Story.WrapList stories, Calendar dateCalendar) {
        int storiesSize = stories.getStories().size();
        List<Story> pendingStories = StoryflowApplication.getPendingStoriesManager().getStories(dateCalendar, period);
        displayedPendingStories.addAll(pendingStories);
        if (pendingStories.isEmpty() && stories.getStories().isEmpty()) {
            Story emptyStory = new Story();
            emptyStory.setFillType(Story.FillType.Empty);
            emptyStory.setDate(dateCalendar.getTime());
            storiesList.add(emptyStory);
            nextStoryDate = null;
        } else {
            Story.WrapList storiesWrapList = new Story.WrapList();
            if (!pendingStories.isEmpty()) {
                storiesWrapList.addStories(pendingStories);
            }
            if (!stories.getStories().isEmpty()) {
                storiesWrapList.addStories(stories.getStories());
                storiesWrapList.setNextStoryStartDate(stories.getNextStoryStartDate());
                storiesWrapList.setPreviousStoryStartDate(stories.getPreviousStoryStartDate());
            }
            addNotEmptyStories(storiesWrapList, storiesSize);
        }
    }

    public void addMoreStories(Story.WrapList stories) {
        if (!stories.getStories().isEmpty()) {
            addNotEmptyStories(stories, stories.getStories().size());
        } else {
            nextStoryDate = null;
        }
    }

    public void addNotEmptyStories(Story.WrapList stories, int size) {
        storiesList.addAll(stories.getStories());
        if (size%limit == 0 && !TextUtils.isEmpty(stories.getNextStoryStartDate())) {
            nextStoryDate = stories.getNextStoryStartDate();
        } else {
            nextStoryDate = null;
        }
    }

    @Nullable
    public String getNextStoryDate() {
        return nextStoryDate;
    }

    @NonNull
    public Calendar getCurrentCalendarDate() {
        Story story = storiesList.get(storiesList.size() - 1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(story.getDate());
        return calendar;
    }

    @NonNull
    public Calendar getPreviousCalendarDate() {
        Calendar calendar = getCurrentCalendarDate();
        switch (period) {
            case Daily:
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                break;
            case Monthly:
                calendar.add(Calendar.MONTH, -1);
                break;
            case Yearly:
                calendar.add(Calendar.YEAR, -1);
                break;
            default:
                throw new UnsupportedOperationException("unsupported itemType is using");
        }
        return calendar;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        switch (viewType) {
            case FILLED_STORY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_stories_item, parent, false);
                return new ReadingStoryHolder(view);
            case LOADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_stories_progress, parent, false);
                return new ReadingStoryProgressHolder(view);
            case EMPTY_STORY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_empty_stories, parent, false);
                return new ReadingStoryEmptyHolder(view);
            default:
                throw new UnsupportedOperationException("You are using unsupported view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case FILLED_STORY:
                holder.itemView.setPadding(0, getItemTopPadding(position), 0, 0);
                Story story = storiesList.get(position);
                ((ReadingStoryHolder)holder).configureUi(story, context);
                break;
            case LOADING:
                break;
            case EMPTY_STORY:
                holder.itemView.setPadding(0, getItemTopPadding(position), 0, 0);
                break;
            default:
                throw new UnsupportedOperationException("You are using unsupported view type");
        }
    }

    @Override
    public int getItemCount() {
        int displayedItemsCount = storiesList.size();
        return displayedItemsCount + 1;
    }

    @Override
    public long getHeaderId(int position) {
        if (getItemViewType(position) == LOADING) {
            position = position - 1;
        }
        Story story = storiesList.get(position);
        Time time = new Time(story.getDate());
        switch (period) {
            case Daily:
                return time.getDayStart().roundToMillis();
            case Monthly:
                return time.getMonthStart().roundToMillis();
            case Yearly:
                return time.getYearStart().roundToMillis();

            default:
                throw new UnsupportedOperationException("You are using unsupported period type");
        }
    }

    @Override
    public ReadingStoryHeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_reading_stories_header, parent, false);
        return new ReadingStoryHeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(ReadingStoryHeaderHolder headerHolder, int position) {
        Story story = storiesList.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(story.getDate());
        switch (period) {
            case Daily:
                DateUtils.getDailyRepresentation(calendar, headerHolder::setDateRepresentation);
                break;
            case Monthly:
                DateUtils.getMonthlyRepresentation(calendar, headerHolder::setDateRepresentation);
                break;
            case Yearly:
                DateUtils.getYearlyRepresentation(calendar, headerHolder::setDateRepresentation);
                break;
            default:
                throw new UnsupportedOperationException("unsupported itemType is using");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return LOADING;
        } else {
            Story story = storiesList.get(position);
            switch (story.getFillType()) {
                case Filled:
                    return FILLED_STORY;
                case Empty:
                    return EMPTY_STORY;
                default:
                    throw new UnsupportedOperationException("You are using unsupported Fill Story type");
            }
        }
    }

    private int getItemTopPadding(int position) {
        if (position == 0) {
            return 0;
        }
        return getHeaderId(position) != getHeaderId(position - 1)
                ? StoryflowApplication.resources().getDimensionPixelSize(R.dimen.headerHeight)
                : 0;

    }

    public void onStoryCreationFailed(PendingStory pendingStory) {
        for (Story story : displayedPendingStories) {
            if (story.getLocalUid().equals(pendingStory.getLocalUid())) {
                int itemPosition = storiesList.indexOf(story);
                if (itemPosition != ArrayUtils.EMPTY_POSITION) {
                    Story displayedStory = storiesList.get(itemPosition);
                    displayedStory.setPendingStatus(PendingStory.Status.CreateFailed);
                    notifyDataSetChanged();
                }
                break;
            }
        }
    }

    public void onPendingStoryDelete(PendingStory pendingStory) {
        for (Story story : displayedPendingStories) {
            if (story.getLocalUid().equals(pendingStory.getLocalUid())) {
                int itemPosition = storiesList.indexOf(story);
                if (itemPosition != ArrayUtils.EMPTY_POSITION) {
                    storiesList.remove(itemPosition);
                    notifyItemRemoved(itemPosition);
                }
                break;
            }
        }
    }

    public static class ReadingStoryHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView descriptionTextView;
        View pendingActionsContainer;
        View retryButton;
        View deleteButton;
        private String storyLocalUid;

        private ImageView authorAvatarImageView;
        private TextView authorFullNameTextView;
        private TextView authorUserNameTextView;
        private View storyImageContainer;

        public ReadingStoryHolder(View itemView) {
            super(itemView);
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

        public void configureUi(Story story, Context context) {
            configureAuthor(story.getAuthor(), context);
            descriptionTextView.setText(story.getDescription());
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

            float coef = MathUtils.calculateMaxScaleRatio(imageWidth, imageHeight, DeviceUtils.getDisplayWidth());
            height = Math.round(coef * imageHeight);

            configureImage(context, imageUrl, height, scaleType);
            configurePendingActions(story.getPendingStatus());
        }

        private void configureAuthor(Author author, Context context) {
            authorUserNameTextView.setText(author.getUserName());
            authorFullNameTextView.setText(author.getFirstName() + " " + author.getLastName());
            Glide
                    .with(context)
                    .load(author.getCroppedAvatar().getImageUrl())
                    .bitmapTransform(new CropCircleTransformation(context))
                    .dontAnimate()
                    .into(authorAvatarImageView);
        }

        private void configureImage(Context context, String url, int height, ImageView.ScaleType scaleType) {
            //TODO
            //scaleType be removed after story.getAuthor().getCroppedImageCover().getRect()
            //fixed: -180x106x735x391
            imageView.setScaleType(scaleType);
            ViewUtils.applyHeight(imageView, height);
            int avatarHeight = context.getResources().getDimensionPixelOffset(R.dimen.avatarDiameterReadingStories);
            ViewUtils.applyHeight(storyImageContainer, height + avatarHeight/2);

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

    public static class ReadingStoryHeaderHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView boldDateTextView;

        public ReadingStoryHeaderHolder(View itemView) {
            super(itemView);
            boldDateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_reading_stories_header_bold_text);
            dateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_reading_stories_header_textview);
        }

        public void setDateRepresentation(String boldText, String formattedDate) {
            dateTextView.setText(formattedDate);
            boldDateTextView.setText(boldText);
        }
    }

    public static class ReadingStoryProgressHolder extends RecyclerView.ViewHolder {
        public ReadingStoryProgressHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ReadingStoryEmptyHolder extends RecyclerView.ViewHolder {
        public ReadingStoryEmptyHolder(View itemView) {
            super(itemView);
        }
    }
}
