package com.thirdandloom.storyflow.adapters;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.DateUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.views.OnSwipeStartNotifyRefreshLayout;
import com.thirdandloom.storyflow.views.recyclerview.VerticalDragNotifierRecyclerView;
import com.thirdandloom.storyflow.views.recyclerview.DisableScrollLinearLayoutManager;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class PeriodsAdapter extends RecyclerView.Adapter<PeriodsAdapter.StoryHolder> {
    public enum ItemType {
        Large, Small
    }
    public enum PeriodType {
        Daily, Monthly, Yearly
    }

    private final Handler postponeHandler = new Handler();
    private final StoriesManager storiesManager;

    private ItemType itemType = ItemType.Large;
    private PeriodType periodType = PeriodType.Daily;
    private Context context;
    private int centerPosition;
    private StoryHolder.Actions storyPreviewActions;
    private boolean fetchedStories;
    private List<BrowseStoriesAdapter> displayingAdapters = new ArrayList<>();

    public PeriodsAdapter(Context context, @Nullable LinkedHashMap<Calendar, Story.WrapList> store,
            @Nullable List<Integer> fetchedPositions, @Nullable StoriesManager.RequestData requestData) {
        this.context = context;
        this.storiesManager = new StoriesManager(store, fetchedPositions, requestData);
    }

    public void setStoryPreviewActions(StoryHolder.Actions storyPreviewActions) {
        this.storyPreviewActions = storyPreviewActions;
    }

    public StoriesManager getStoriesManager() {
        return storiesManager;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setCenterPosition(int centerPosition) {
        this.centerPosition = centerPosition;
    }

    public void onNewStoriesFetched(Story.WrapList list, Calendar calendar) {
        fetchedStories = true;
        storiesManager.storeData(calendar, list);
        updateDataFromLocalStore();
    }

    public void onNewStoriesFetchFailed(int position) {
        fetchedStories = true;
        storiesManager.removeFromFetchedPositions(position);
        updateDataFromLocalStore();
    }

    public void updatePeriodType() {
        switch (storiesManager.getRequestData().getPeriodType()) {
            case Yearly:
                periodType = PeriodType.Yearly;
                break;
            case Monthly:
                periodType = PeriodType.Monthly;
                break;
            case Daily:
                periodType = PeriodType.Daily;
                break;
        }
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public void clearData() {
        this.storiesManager.clearStore();
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void changeItemWidth() {
        switch (itemType) {
            case Large:
                itemType = ItemType.Small;
                break;
            case Small:
                itemType = ItemType.Large;
                break;
        }
    }

    public int getCenterPosition() {
        return centerPosition;
    }

    public int getItemWidthPixel() {
        switch (itemType) {
            case Large:
                return DeviceUtils.getDisplayWidth()-getItemPadding()*2;
            case Small:
                return DeviceUtils.getDisplayWidth()/2;
        }
        throw new UnsupportedOperationException("unsupported itemWidth is using");
    }

    public void deleteStory(PendingStory story) {
        for (BrowseStoriesAdapter adapter : displayingAdapters) {
            Date adapterDate = adapter.getCurrentDate();
            Date storyDate = story.getDate();
            if (adapterDate.equals(storyDate)) {
                adapter.deleteStory(story);
                break;
            }
        }
    }

    @Override
    public StoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_main_horizontal, parent, false);
        StoryHolder storyHolder = new StoryHolder(v, storyPreviewActions);
        return storyHolder;
    }

    @Override
    public void onBindViewHolder(StoryHolder storyHolder, int position) {
        ViewUtils.applyWidth(storyHolder.itemView, getItemWidthPixel());
        Calendar storyDate = updateDate(storyHolder, position, centerPosition, periodType);

        RecyclerView.Adapter previousAdapter = storyHolder.recyclerView.getAdapter();
        if (previousAdapter != null) {
            displayingAdapters.remove(previousAdapter);
        }

        BrowseStoriesAdapter adapter;
        if (!storyDate.equals(storyHolder.getDateCalendar())) {
            storyHolder.setDateCalendar(storyDate);
            adapter = new BrowseStoriesAdapter(context, storiesManager.getDisplayingStories(storyDate), getItemWidthPixel());
            storyHolder.recyclerView.setAdapter(adapter);
        } else {
            adapter = (BrowseStoriesAdapter)storyHolder.recyclerView.getAdapter();
            adapter.setData(storiesManager.getDisplayingStories(storyDate), getItemWidthPixel());
            adapter.notifyDataSetChanged();
        }
        adapter.setCurrentDate(storyDate.getTime());
        displayingAdapters.add(adapter);

        ViewUtils.setHidden(storyHolder.progressBar, adapter.getDataType() != BrowseStoriesAdapter.DataType.PendingStories);
        storyHolder.updateEmptyView(adapter.getDataType());
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public void updateDataFromLocalStore() {
        if (fetchedStories) {
            fetchedStories = false;
            final Runnable r = this::notifyDataSetChanged;
            postponeHandler.post(r);
        }
    }

    public static class StoryHolder extends RecyclerView.ViewHolder {
        public interface Actions {
            void onDragStarted();
            void onDragFinished(int velocity);
            void pullToRefreshMotionNotifier(int motionEventAction);
            void onDrag(float scrollAbsolute, float scrollDelta, View scrollingView, Calendar calendar);
            void onClick(View view, Calendar calendar);
            void onPullToRefreshStarted(SwipeRefreshLayout refreshLayout, Calendar calendar);
        }

        private TextView dateTextView;
        private TextView boldDateTextView;
        private VerticalDragNotifierRecyclerView recyclerView;
        private View progressBar;
        private View noStoriesView;
        private OnSwipeStartNotifyRefreshLayout refreshLayout;
        private Calendar dateCalendar;

        public StoryHolder(View itemView, Actions actions) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_text_view);
            recyclerView = (VerticalDragNotifierRecyclerView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_recycler_view);
            boldDateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_bold_text_view);
            progressBar = itemView.findViewById(R.id.adapter_recycler_item_horizontal_recycler_progress_bar);
            refreshLayout = (OnSwipeStartNotifyRefreshLayout) itemView.findViewById(R.id.adapter_recycler_item_horizontal_refresh_layout);
            noStoriesView = itemView.findViewById(R.id.adapter_recycler_item_horizontal_no_stories_view);

            refreshLayout.setColorSchemeResources(R.color.yellow, R.color.grey);
            refreshLayout.setOnRefreshListener(() -> {
                actions.onPullToRefreshStarted(refreshLayout, dateCalendar);
            });
            refreshLayout.setNotifier(actions::pullToRefreshMotionNotifier);

            DisableScrollLinearLayoutManager manager = new DisableScrollLinearLayoutManager(itemView.getContext());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            manager.setDisableScroll(true);

            recyclerView.setLayoutManager(manager);
            recyclerView.setDragStarted(actions::onDragStarted);
            recyclerView.setDragFinished(actions::onDragFinished);
            recyclerView.setOnDrag((current, delta, view) -> {
                actions.onDrag(current, delta, view, getDateCalendar());
            });
            recyclerView.setOnClick(() -> {
                actions.onClick(recyclerView, getDateCalendar());
            });

            itemView.setOnClickListener(v -> {
                actions.onClick(recyclerView, getDateCalendar());
            });
        }

        public void setDateCalendar(Calendar dateCalendar) {
            this.dateCalendar = dateCalendar;
        }

        public Calendar getDateCalendar() {
            return dateCalendar;
        }

        public void updateEmptyView(BrowseStoriesAdapter.DataType dataType) {
            ViewUtils.setShown(noStoriesView, dataType == BrowseStoriesAdapter.DataType.EmptyStories);
            int backgroundColorId = dataType == BrowseStoriesAdapter.DataType.EmptyStories
                    ? R.color.greyLightest
                    : R.color.transparent;
            recyclerView.setBackgroundColor(StoryflowApplication.resources().getColor(backgroundColorId));
        }

        public void setDateRepresentation(String boldText, String formattedDate) {
            dateTextView.setText(formattedDate);
            boldDateTextView.setText(boldText);
        }
    }

    @NonNull
    private static Calendar updateDate(StoryHolder storyHolder, int position, int centerPosition, PeriodType periodType) {
        Calendar calendar = getDateCalendar(position, centerPosition, periodType);
        switch (periodType) {
            case Daily:
                DateUtils.getDailyRepresentation(calendar, storyHolder::setDateRepresentation);
                break;
            case Monthly:
                DateUtils.getMonthlyRepresentation(calendar, storyHolder::setDateRepresentation);
                break;
            case Yearly:
                DateUtils.getYearlyRepresentation(calendar, storyHolder::setDateRepresentation);
                break;
            default:
                throw new UnsupportedOperationException("unsupported itemType is using");
        }
        return calendar;
    }

    public static Calendar getDateCalendar(int position, int centerPosition, PeriodType periodType) {
        Calendar calendar = DateUtils.todayCalendar();
        int offset = position - centerPosition;
        switch (periodType) {
            case Daily:
                calendar.add(Calendar.DAY_OF_YEAR, offset);
                break;
            case Monthly:
                calendar.add(Calendar.MONTH, offset);
                break;
            case Yearly:
                calendar.add(Calendar.YEAR, offset);
                break;
            default:
                throw new UnsupportedOperationException("unsupported itemType is using");
        }
        return calendar;
    }

    public static int getItemPadding() {
        return StoryflowApplication.resources().getDimensionPixelOffset(R.dimen.sizeNormal);
    }

    public static int getItemMargin() {
        return StoryflowApplication.resources().getDimensionPixelOffset(R.dimen.sizeTiny);
    }
}
