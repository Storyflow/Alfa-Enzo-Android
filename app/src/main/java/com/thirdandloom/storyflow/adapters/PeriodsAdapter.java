package com.thirdandloom.storyflow.adapters;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.managers.StoriesManager;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

public class PeriodsAdapter extends RecyclerView.Adapter<PeriodsAdapter.StoryHolder> {

    public enum ItemWidth {
        Large, Small
    }
    public enum ItemType {
        Daily, Monthly, Yearly
    }

    private final Handler postponeHandler = new Handler();
    private final StoriesManager storiesManager = new StoriesManager();

    private ItemWidth itemWidth = ItemWidth.Large;
    private ItemType itemType = ItemType.Daily;
    private Context context;
    private int centerPosition;
    private StoryHolder.Actions storyPreviewActions;
    private boolean fetchedStories;

    public PeriodsAdapter(Context context) {
        this.context = context;
    }

    public void setStoryPreviewActions(StoryHolder.Actions storyPreviewActions) {
        this.storyPreviewActions = storyPreviewActions;
    }

    public StoriesManager getStoriesManager() {
        return storiesManager;
    }

    public ItemWidth getItemWidth() {
        return itemWidth;
    }

    public void setCenterPosition(int centerPosition) {
        this.centerPosition = centerPosition;
    }

    public void onNewStoriesFetched(Story.WrapList list, Calendar calendar) {
        fetchedStories = true;
        storiesManager.storeData(calendar, list);
        updateDataFromLocalStore();
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
        this.storiesManager.clearStore();
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void changeItemWidth() {
        switch (itemWidth) {
            case Large:
                itemWidth = ItemWidth.Small;
                break;
            case Small:
                itemWidth = ItemWidth.Large;
                break;
        }
    }

    public static int getItemMargin() {
        return StoryflowApplication.resources().getDimensionPixelOffset(R.dimen.sizeTiny);
    }

    public int getCenterPosition() {
        return centerPosition;
    }

    public int getItemWidthPixel() {
        switch (itemWidth) {
            case Large:
                int itemPadding = StoryflowApplication.resources().getDimensionPixelOffset(R.dimen.sizeNormal);
                return DeviceUtils.getDisplayWidth()-itemPadding*2;
            case Small:
                return DeviceUtils.getDisplayWidth()/2;
        }
        throw new UnsupportedOperationException("unsupported itemWidth is using");
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
        Calendar storyDate = updateDate(storyHolder, position, centerPosition, itemType);
        StoriesPreviewAdapter adapter = new StoriesPreviewAdapter(context, storiesManager.getStories(storyDate));
        storyHolder.recyclerView.setAdapter(adapter);
        ViewUtils.setHidden(storyHolder.progressBar, adapter.getDataType() != StoriesPreviewAdapter.DataType.PendingStories);
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
            void onDrag(float scrollAbsolute, float scrollDelta, View scrollingView);
            void onClick(View view);
        }

        private TextView dateTextView;
        private TextView boldDateTextView;
        private VerticalDragNotifierRecyclerView recyclerView;
        private View progressBar;
        private View noStoriesView;
        private OnSwipeStartNotifyRefreshLayout refreshLayout;

        public StoryHolder(View itemView, Actions actions) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_text_view);
            recyclerView = (VerticalDragNotifierRecyclerView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_recycler_view);
            boldDateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_bold_text_view);
            progressBar = itemView.findViewById(R.id.adapter_recycler_item_horizontal_recycler_progress_bar);
            refreshLayout = (OnSwipeStartNotifyRefreshLayout) itemView.findViewById(R.id.adapter_recycler_item_horizontal_refresh_layout);
            noStoriesView = itemView.findViewById(R.id.adapter_recycler_item_horizontal_no_stories_view);

            initRefreshLayout(refreshLayout, actions);

            DisableScrollLinearLayoutManager manager = new DisableScrollLinearLayoutManager(itemView.getContext());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            manager.setDisableScroll(true);

            recyclerView.setLayoutManager(manager);
            recyclerView.setDragStarted(actions::onDragStarted);
            recyclerView.setDragFinished(actions::onDragFinished);
            recyclerView.setOnDrag(actions::onDrag);
            recyclerView.setOnClick(() -> actions.onClick(recyclerView));

            itemView.setOnClickListener(v -> actions.onClick(recyclerView));
        }

        public void updateEmptyView(StoriesPreviewAdapter.DataType dataType) {
            ViewUtils.setShown(noStoriesView, dataType == StoriesPreviewAdapter.DataType.EmptyStories);
            int backgroundColorId = dataType == StoriesPreviewAdapter.DataType.EmptyStories
                    ? R.color.greyLightest
                    : R.color.transparent;
            recyclerView.setBackgroundColor(StoryflowApplication.resources().getColor(backgroundColorId));
        }

        public void setDateRepresentation(String boldText, String formattedDate) {
            dateTextView.setText(formattedDate);
            boldDateTextView.setText(boldText);
        }
    }

    private static void initRefreshLayout(OnSwipeStartNotifyRefreshLayout refreshLayout, StoryHolder.Actions actions) {
        refreshLayout.setColorSchemeResources(R.color.yellow, R.color.grey);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
        });
        refreshLayout.setNotifier(actions::pullToRefreshMotionNotifier);
    }

    @NonNull
    private static Calendar updateDate(StoryHolder storyHolder, int position, int centerPosition, ItemType itemType) {
        Calendar calendar = getDateCalendar(position, centerPosition, itemType);
        switch (itemType) {
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

    public static Calendar getDateCalendar(int position, int centerPosition, ItemType itemType) {
        Calendar calendar = DateUtils.todayCalendar();
        int offset = position - centerPosition;
        switch (itemType) {
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
}
