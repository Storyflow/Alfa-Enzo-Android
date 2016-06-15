package com.thirdandloom.storyflow.adapters;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesBaseViewHolder;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesEmptyViewHolder;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesHeaderViewHolder;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesPendingViewHolder;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesPopulatedViewHolder;
import com.thirdandloom.storyflow.managers.StoriesManager.RequestData;
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
    private static final int EMPTY_STORY = FILLED_STORY + 1;
    private static final int LOADING = EMPTY_STORY + 1;

    private LinkedList<Story> storiesList = new LinkedList<>();
    private ArrayList<Story> displayedPendingStories = new ArrayList<>();

    private String nextStoryDate;
    private final int limit;
    private final RequestData.Period.Type period;
    private final Context context;

    public ReadStoriesAdapter(Story.WrapList stories, Calendar dateCalendar, RequestData requestData, Context context) {
        this.limit = requestData.getLimit();
        this.period = requestData.getPeriodType();
        this.context = context;
        addNewStories(stories, dateCalendar);
    }

    @NonNull
    public Story getStory(int position) {
        return storiesList.get(position);
    }

    public View getFromView(int position, RecyclerView.ViewHolder holder) {
        switch (getItemViewType(position)) {
            case FILLED_STORY:
                return ((ReadStoriesPopulatedViewHolder) holder).imageView;
            case LOADING:
                return holder.itemView;
            case EMPTY_STORY:
                return holder.itemView;
            default:
                throw new UnsupportedOperationException("You are using unsupported item view type");
        }
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
                return ReadStoriesPopulatedViewHolder.newInstance(parent, new ReadStoriesPopulatedViewHolder.Actions() {
                    @Override
                    public void onStarClicked(int adapterPosition) {
                        notifyItemChanged(adapterPosition);
                    }
                });
            case LOADING:
                return ReadStoriesPendingViewHolder.newInstance(parent);
            case EMPTY_STORY:
                return ReadStoriesEmptyViewHolder.newInstance(parent);
            default:
                throw new UnsupportedOperationException("You are using unsupported view type");
        }
    }

    @Override
    public void onBindViewHolder(ReadStoriesBaseViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case FILLED_STORY:
                holder.itemView.setPadding(0, getItemTopPadding(position), 0, 0);
                Story story = storiesList.get(position);
                holder.initGui(context, story);
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
}
