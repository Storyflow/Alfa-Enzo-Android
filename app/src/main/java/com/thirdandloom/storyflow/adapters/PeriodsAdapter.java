package com.thirdandloom.storyflow.adapters;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.DateUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.views.OnSwipeStartNotifyRefreshLayout;
import com.thirdandloom.storyflow.views.recyclerview.VerticalDragNotifierRecyclerView;
import com.thirdandloom.storyflow.views.recyclerview.DisableScrollLinearLayoutManager;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action3;

import android.content.Context;
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

    private ItemWidth itemWidth = ItemWidth.Large;
    private ItemType itemType = ItemType.Daily;
    private Context context;
    private int centerPosition;

    private Action1<Integer> pullToRefreshNotifier;
    private Action0 onChildDragStarted;
    private Action1<Integer> onChildDragFinished;
    private Action3<Float, Float, View> onChildDrag;
    private Action1<View> onChildClick;

    public PeriodsAdapter(Context context) {
        this.context = context;
    }

    public void setPullToRefreshNotifier(Action1<Integer> pullToRefreshNotifier) {
        this.pullToRefreshNotifier = pullToRefreshNotifier;
    }

    public void setOnChildDragFinished(Action1<Integer> onChildDragFinished) {
        this.onChildDragFinished = onChildDragFinished;
    }

    public void setOnChildDragStarted(Action0 onChildDragStarted) {
        this.onChildDragStarted = onChildDragStarted;
    }

    public void setOnChildDrag(Action3<Float, Float, View> onChildDrag) {
        this.onChildDrag = onChildDrag;
    }

    public void setOnChildClick(Action1<View> onChildClick) {
        this.onChildClick = onChildClick;
    }

    public ItemWidth getItemWidth() {
        return itemWidth;
    }

    public void setCenterPosition(int centerPosition) {
        this.centerPosition = centerPosition;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
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
        StoryHolder storyHolder = new StoryHolder(v, pullToRefreshNotifier, onChildDragStarted, onChildDragFinished, onChildDrag, onChildClick);
        return storyHolder;
    }

    @Override
    public void onBindViewHolder(StoryHolder storyHolder, int position) {
        ViewUtils.applyWidth(storyHolder.itemView, getItemWidthPixel());
        updateDate(storyHolder, position, centerPosition, itemType);

        RecyclerView.Adapter adapter = new StoriesPreviewAdapter(context);
        storyHolder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public static class StoryHolder extends RecyclerView.ViewHolder {

        private TextView dateTextView;
        private TextView boldDateTextView;
        private VerticalDragNotifierRecyclerView recyclerView;

        public StoryHolder(View itemView, Action1<Integer> pullToRefreshNotifier, Action0 startDrag, Action1<Integer> finishDrag, Action3<Float, Float, View> onDrag, Action1<View> onClick) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_text_view);
            recyclerView = (VerticalDragNotifierRecyclerView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_recycler_view);
            boldDateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_bold_text_view);
            OnSwipeStartNotifyRefreshLayout refreshLayout = (OnSwipeStartNotifyRefreshLayout)itemView.findViewById(R.id.adapter_recycler_item_horizontal_refresh_layout);
            refreshLayout.setColorSchemeResources(R.color.yellow, R.color.grey);
            refreshLayout.setOnRefreshListener(() -> {
                refreshLayout.setRefreshing(false);
            });
            refreshLayout.setNotifier(pullToRefreshNotifier);

            DisableScrollLinearLayoutManager manager = new DisableScrollLinearLayoutManager(itemView.getContext());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            manager.setDisableScroll(true);
            recyclerView.setLayoutManager(manager);

            recyclerView.setDragStarted(startDrag);
            recyclerView.setDragFinished(finishDrag);
            recyclerView.setOnDrag(onDrag);

            itemView.setOnClickListener(v -> onClick.call(recyclerView));
            recyclerView.setOnClick(() -> onClick.call(recyclerView));
        }

        public void setDateRepresentation(String boldText, String formattedDate) {
            dateTextView.setText(formattedDate);
            boldDateTextView.setText(boldText);
        }
    }

    private static void updateDate(StoryHolder storyHolder, int position, int centerPosition, ItemType itemType) {
        Calendar calendar = DateUtils.todayCalendar();
        int offset = position - centerPosition;
        switch (itemType) {
            case Daily:
                calendar.add(Calendar.DAY_OF_YEAR, offset);
                DateUtils.getDailyRepresentation(calendar, storyHolder::setDateRepresentation);
                break;
            case Monthly:
                calendar.add(Calendar.MONTH, offset);
                DateUtils.getMonthlyRepresentation(calendar, storyHolder::setDateRepresentation);
                break;
            case Yearly:
                calendar.add(Calendar.YEAR, offset);
                DateUtils.getYearlyRepresentation(calendar, storyHolder::setDateRepresentation);
                break;
            default:
                throw new UnsupportedOperationException("unsupported itemType is using");
        }
    }
}
