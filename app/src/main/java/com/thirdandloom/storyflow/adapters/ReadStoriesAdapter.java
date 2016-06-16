package com.thirdandloom.storyflow.adapters;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesBaseViewHolder;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesEmptyViewHolder;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesHeaderViewHolder;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesPendingViewHolder;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesPopulatedViewHolder;
import com.thirdandloom.storyflow.managers.StoriesManager.RequestData;
import com.thirdandloom.storyflow.models.Likes;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.DateUtils;
import com.thirdandloom.storyflow.utils.models.Time;
import com.thirdandloom.storyflow.views.recyclerview.decoration.StickyHeaderAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ReadStoriesAdapter extends RecyclerView.Adapter<ReadStoriesBaseViewHolder> implements StickyHeaderAdapter<ReadStoriesHeaderViewHolder> {

    public static final int FILLED_STORY = 0;
    public static final int FILLED_STORY_WITH_HEADER = FILLED_STORY + 1;
    public static final int EMPTY_STORY_WITH_HEADER = FILLED_STORY_WITH_HEADER + 1;
    private static final int LOADING = EMPTY_STORY_WITH_HEADER + 1;

    public interface Actions {
        void startPreview(Story story, View fromView);
        void likeClicked(Story story, Likes likes, ReadStoriesPopulatedViewHolder holder);
    }

    private LinkedList<Story> storiesList = new LinkedList<>();
    private ArrayList<Story> displayedPendingStories = new ArrayList<>();

    private String nextStoryDate;
    private final int limit;
    private final RequestData.Period.Type period;
    private final Context context;
    private final ReadStoriesAdapter.Actions actions;

    public ReadStoriesAdapter(Context context, Story.WrapList stories, Calendar dateCalendar, RequestData requestData, ReadStoriesAdapter.Actions actions) {
        this.limit = requestData.getLimit();
        this.period = requestData.getPeriodType();
        this.context = context;
        this.actions = actions;
        addNewStories(stories, dateCalendar);
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
            notifyItemInserted(storiesList.size() - 2);
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
        if (size % limit == 0 && !TextUtils.isEmpty(stories.getNextStoryStartDate())) {
            nextStoryDate = stories.getNextStoryStartDate();
        } else {
            nextStoryDate = null;
        }
        notifyItemRangeInserted(getItemCount() - 1, stories.getStories().size());
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
    public ReadStoriesBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FILLED_STORY:
                return ReadStoriesPopulatedViewHolder.newInstance(parent, populatedHolderActions);
            case FILLED_STORY_WITH_HEADER:
                ReadStoriesPopulatedViewHolder holderWithHeader = ReadStoriesPopulatedViewHolder.newInstance(parent, populatedHolderActions);
                holderWithHeader.itemView.setPadding(0, StoryflowApplication.resources().getDimensionPixelSize(R.dimen.headerHeight), 0, 0);
                return holderWithHeader;
            case LOADING:
                return ReadStoriesPendingViewHolder.newInstance(parent);
            case EMPTY_STORY_WITH_HEADER:
                return ReadStoriesEmptyViewHolder.newInstance(parent);

            default:
                throw new UnsupportedOperationException("You are using unsupported view type");
        }
    }

    @Override
    public void onBindViewHolder(ReadStoriesBaseViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case FILLED_STORY:
            case FILLED_STORY_WITH_HEADER:
                Story story = storiesList.get(position);
                holder.initGui(context, story);
                break;
            case LOADING:
            case EMPTY_STORY_WITH_HEADER:
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
        if (isLoadingPosition(position)) {
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
    public ReadStoriesHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return ReadStoriesHeaderViewHolder.newInstance(parent);
    }

    @Override
    public void onBindHeaderViewHolder(ReadStoriesHeaderViewHolder headerHolder, int position) {
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
        if (isLoadingPosition(position)) {
            return LOADING;
        } else {
            Story story = storiesList.get(position);
            switch (story.getFillType()) {
                case Filled:
                    return position != 0 && getHeaderId(position) != getHeaderId(position - 1)
                                    ? FILLED_STORY_WITH_HEADER
                                    : FILLED_STORY;
                case Empty:
                    return EMPTY_STORY_WITH_HEADER;
                default:
                    throw new UnsupportedOperationException("You are using unsupported Fill Story type");
            }
        }
    }

    private boolean isLoadingPosition(int position) {
        return position == getItemCount() - 1;
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

    public void likeActionFailed(Story story, ReadStoriesPopulatedViewHolder holder) {
        Likes newLikes = switchStoryLikes(story);
        if (holder.getStory().equals(story)) {
            holder.onStoryLikesChanged(newLikes);
        }
    }

    private Likes switchStoryLikes(Story story) {
        if (story.getLikes() == null) {
            story.setLikes(new Likes());
        }
        return story.getLikes().switchCurrentUserLike();
    }

    private final ReadStoriesPopulatedViewHolder.Actions populatedHolderActions = new ReadStoriesPopulatedViewHolder.Actions() {
        @Override
        public Likes onStarClicked(int adapterPosition, ReadStoriesPopulatedViewHolder holder) {
            Story story = storiesList.get(adapterPosition);
            Likes newLikes = switchStoryLikes(story);
            actions.likeClicked(story, newLikes, holder);
            return newLikes;
        }

        @Override
        public void notifyItemChanged(int adapterPosition) {
            ReadStoriesAdapter.this.notifyItemChanged(adapterPosition);
        }

        @Override
        public void onImageClicked(int adapterPosition, View view) {
            actions.startPreview(storiesList.get(adapterPosition), view);
        }
    };
}
