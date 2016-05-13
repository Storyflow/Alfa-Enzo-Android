package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.adapters.PeriodsAdapter;
import com.thirdandloom.storyflow.fragments.StoryDetailsFragment;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AnimationUtils;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.RecyclerLayoutManagerUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.views.TabBar;
import com.thirdandloom.storyflow.views.recyclerview.DisableScrollLinearLayoutManager;
import com.thirdandloom.storyflow.views.recyclerview.SnappyLinearLayoutManager;
import com.thirdandloom.storyflow.views.recyclerview.SnappyRecyclerView;
import com.thirdandloom.storyflow.views.toolbar.BrowseStoriesToolBar;
import rx.functions.Action1;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

public class BrowseStoriesActivity extends BaseActivity implements StoryDetailsFragment.IStoryDetailFragmentDataSource {
    private SavedState state;

    private SnappyRecyclerView horizontalRecyclerView;
    private View periodChooserView;
    private TabBar tabBar;
    private Action1<Float> takeScrollValue;
    private StoryDetailsFragment storyDetailsFragment;
    private int recyclerViewScrollState = RecyclerView.SCROLL_STATE_IDLE;

    public static Intent newInstance(boolean continueAnimation) {
        Intent intent = new Intent(StoryflowApplication.applicationContext, BrowseStoriesActivity.class);
        SavedState state = new SavedState();
        state.continueAnimation = continueAnimation;
        putExtra(intent, state);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_stories);
        state = (SavedState) getState();
        restoreState(savedInstanceState, restoredState -> {
            state = (SavedState) restoredState;
        });
        if (state.continueAnimation) {
            initLaunchAnimation(savedInstanceState);
        } else {
            ViewUtils.hide(findViewById(R.id.launch_layout));
        }
        findViews();
        initGui();
    }

    private void findViews() {
        horizontalRecyclerView = (SnappyRecyclerView) findViewById(R.id.activity_browse_stories_horizontal_recycler_view);
        periodChooserView = findViewById(R.id.activity_browse_stories_period_chooser);
        tabBar = (TabBar) findViewById(R.id.activity_browse_stories_tab_bar);
    }

    private void initGui() {
        initToolBar();
        initHorizontalRecyclerView();
        initPeriodChooser();
    }

    private void initPeriodChooser() {
        periodChooserView.getLayoutParams().height = 0;
        periodChooserView.requestLayout();
        periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_yearly).setOnClickListener(v -> {
            getPeriodsAdapter().getStoriesManager().getRequestData().selectPeriodYearly();
            onChangePeriodClicked();
            onPeriodChanged(PeriodsAdapter.PeriodType.Yearly);
        });
        periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_monthly).setOnClickListener(v -> {
            getPeriodsAdapter().getStoriesManager().getRequestData().selectPeriodMonthly();
            onChangePeriodClicked();
            onPeriodChanged(PeriodsAdapter.PeriodType.Monthly);
        });
        periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_daily).setOnClickListener(v -> {
            getPeriodsAdapter().getStoriesManager().getRequestData().selectPeriodDaily();
            onChangePeriodClicked();
            onPeriodChanged(PeriodsAdapter.PeriodType.Daily);
        });
    }

    private void initHorizontalRecyclerView() {
        SnappyLinearLayoutManager layoutManager = new SnappyLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontalRecyclerView.setItemAnimator(new DefaultItemAnimator());
        horizontalRecyclerView.setHasFixedSize(true);
        horizontalRecyclerView.setLayoutManager(layoutManager);

        PeriodsAdapter adapter = new PeriodsAdapter(this, state.savedStore, state.savedFetchedPositions, state.savedRequestData);
        adapter.setStoryPreviewActions(storyPreviewActions);
        adapter.setItemType(state.savedItemType);
        horizontalRecyclerView.setAdapter(adapter);
        ((BrowseStoriesToolBar) getToolbar()).onNewItemWidthSelected(adapter.getItemType());

        int centerPosition = state.currentPosition != ArrayUtils.INDEX_NOT_FOUND
                ? state.currentPosition
                : adapter.getItemCount() / 2;
        updateOffset(centerPosition);
        adapter.setCenterPosition(centerPosition);
        adapter.updatePeriodType();
        loadStoriesBetweenPositions(centerPosition - 1, centerPosition + 1);

        horizontalRecyclerView.addOnScrollListener(tabBar.getRecyclerViewScrollListener());
        horizontalRecyclerView.addOnScrollListener(new OnScrollListener());
        tabBar.setItemWidth(adapter.getItemWidthPixel()+PeriodsAdapter.getItemMargin()*2);
        tabBar.setActions(tabBarActions);
    }

    private void onPeriodChanged(PeriodsAdapter.PeriodType periodType) {
        if (getPeriodsAdapter().getPeriodType() != periodType) {
            int position = RecyclerLayoutManagerUtils.getCurrentVisiblePosition((LinearLayoutManager) horizontalRecyclerView.getLayoutManager());
            getPeriodsAdapter().setCenterPosition(position);
            getPeriodsAdapter().setPeriodType(periodType);
            getPeriodsAdapter().clearData();
            getPeriodsAdapter().notifyDataSetChanged();
            loadStoriesBetweenPositions(position - 1, position + 1);
        }
    }

    private void onChangePeriodClicked() {
        state.choosePeriodIsVisible = !state.choosePeriodIsVisible;
        ViewUtils.getMeasuredSize(periodChooserView, (width, height) -> {
            ValueAnimator valueAnimator = state.choosePeriodIsVisible
                    ? ValueAnimator.ofInt(0, height)
                    : ValueAnimator.ofInt(periodChooserView.getHeight(), 0);
            valueAnimator.setDuration(300);
            valueAnimator.addUpdateListener(animation -> {
                Integer value = (Integer) animation.getAnimatedValue();
                periodChooserView.getLayoutParams().height = value.intValue();
                periodChooserView.requestLayout();
            });
            valueAnimator.setTarget(periodChooserView);
            valueAnimator.start();
        });
    }

    private void onChangeSizeClicked() {
        PeriodsAdapter adapter = getPeriodsAdapter();
        adapter.changeItemWidth();
        adapter.notifyDataSetChanged();
        int position = RecyclerLayoutManagerUtils.getCurrentVisiblePosition((LinearLayoutManager) horizontalRecyclerView.getLayoutManager());
        updateOffset(position);
        tabBar.setItemWidth(adapter.getItemWidthPixel());
        ((BrowseStoriesToolBar) getToolbar()).onNewItemWidthSelected(adapter.getItemType());
    }

    private void updateOffset(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) horizontalRecyclerView.getLayoutManager();
        PeriodsAdapter adapter = getPeriodsAdapter();

        int offset = DeviceUtils.getDisplayWidth() - adapter.getItemWidthPixel() - PeriodsAdapter.getItemMargin() * 2;
        layoutManager.scrollToPositionWithOffset(position, offset / 2);
    }

    private DisableScrollLinearLayoutManager getHorizontalRecyclerViewLayoutManager() {
        return (DisableScrollLinearLayoutManager) horizontalRecyclerView.getLayoutManager();
    }

    private PeriodsAdapter getPeriodsAdapter() {
        return (PeriodsAdapter) horizontalRecyclerView.getAdapter();
    }

    @Override
    protected int getStatusBarColorResourceId() {
        return R.color.greyXLighter;
    }

    @Override
    protected boolean hasToolBar() {
        return true;
    }

    private void initToolBar() {
        BrowseStoriesToolBar toolBar = (BrowseStoriesToolBar) getToolbar();
        toolBar.setOnChangePeriod(this::onChangePeriodClicked);
        toolBar.setOnChangeSize(this::onChangeSizeClicked);
    }

    private void initLaunchAnimation(Bundle savedInstanceState) {
        state.continueAnimation = false;
        View launchView = findViewById(R.id.launch_layout);
        if (savedInstanceState == null) {
            View circleView = launchView.findViewById(R.id.launch_circle_view);
            ViewUtils.getMeasuredSize(findViewById(R.id.launch_text_view), (width, height) -> {
                ViewUtils.setViewFrame(circleView, height, height);
                AnimationUtils.applyStartAnimation(launchView, circleView);
            });
        } else {
            ViewUtils.removeFromParent(launchView);
        }
    }

    @Override
    public List<Integer> getDataSource() {
        return null;
    }

    @Override
    public void setTakeScrollDelta(Action1<Float> takeScroll) {
        this.takeScrollValue = takeScroll;
    }

    @Override
    public void onBackPressed() {
        if (storyDetailsFragment != null && storyDetailsFragment.isAdded()) {
            storyDetailsFragment.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    private class OnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            recyclerViewScrollState = newState;
            if (canUpdateData()) {
                getPeriodsAdapter().updateDataFromLocalStore();
                fetchData();
            }
        }
    }

    private void fetchData() {
        int firstVisiblePosition = getHorizontalRecyclerViewLayoutManager().findFirstVisibleItemPosition();
        int lastVisiblePosition = getHorizontalRecyclerViewLayoutManager().findLastVisibleItemPosition();
        loadStoriesBetweenPositions(firstVisiblePosition, lastVisiblePosition);
    }

    private void loadStoriesBetweenPositions(int firstVisiblePosition, int lastVisiblePosition) {
        int centerPosition = getPeriodsAdapter().getCenterPosition();
        PeriodsAdapter.PeriodType periodType = getPeriodsAdapter().getPeriodType();
        StoriesManager storiesManager = getPeriodsAdapter().getStoriesManager();
        for (int position = firstVisiblePosition; position <= lastVisiblePosition; position++) {
            sendFetchStoriesRequest(position, centerPosition, periodType, storiesManager);
        }
    }

    private void sendFetchStoriesRequest(int position, int centerPosition, PeriodsAdapter.PeriodType periodType, StoriesManager storiesManager) {
        if (!storiesManager.isFetchedPosition(position)) {
            Calendar calendar = PeriodsAdapter.getDateCalendar(position, centerPosition, periodType);
            storiesManager.addFetchedStoryPosition(position);
            StoryflowApplication.restClient().loadStories(storiesManager.getRequestData(calendar), (Story.WrapList list) -> {
                getPeriodsAdapter().onNewStoriesFetched(list, calendar);
            }, (errorMessage, type) -> {
                showError(errorMessage);
                getPeriodsAdapter().onNewStoriesFetchFailed(position);
            });
        }
    }

    private boolean canUpdateData() {
        return recyclerViewScrollState != RecyclerView.SCROLL_STATE_SETTLING;
    }

    private final PeriodsAdapter.StoryHolder.Actions storyPreviewActions = new PeriodsAdapter.StoryHolder.Actions() {
        @Override
        public void onDragStarted() {
            getHorizontalRecyclerViewLayoutManager().setDisableScroll(true);
        }

        @Override
        public void onDragFinished(int velocity) {
            if (storyDetailsFragment != null)
                storyDetailsFragment.onDragFinished(velocity);
            getHorizontalRecyclerViewLayoutManager().setDisableScroll(false);
        }

        @Override
        public void pullToRefreshMotionNotifier(int motionEventAction) {
            getHorizontalRecyclerViewLayoutManager().setDisableScroll(motionEventAction == MotionEvent.ACTION_MOVE);
        }

        @Override
        public void onDrag(float scrollAbsolute, float scrollDelta, View scrollingView) {
            if (takeScrollValue != null)
                takeScrollValue.call(scrollDelta);
            if (scrollAbsolute > 0) {
                if (storyDetailsFragment == null || !storyDetailsFragment.isAdded()) {
                    storyDetailsFragment = StoryDetailsFragment.newInstance(scrollingView, false);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.activity_browse_stories_container, storyDetailsFragment, StoryDetailsFragment.class.getSimpleName());
                    ft.commit();
                }
            } else {
                if (storyDetailsFragment != null) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove(storyDetailsFragment);
                    ft.commit();
                    storyDetailsFragment = null;
                    takeScrollValue = null;
                }
            }
        }

        @Override
        public void onClick(View view) {
            storyDetailsFragment = StoryDetailsFragment.newInstance(view, true);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.activity_browse_stories_container, storyDetailsFragment, StoryDetailsFragment.class.getSimpleName());
            ft.commit();
        }

        @Override
        public void onPullToRefreshStarted(SwipeRefreshLayout refreshLayout, Calendar calendar) {
            StoryflowApplication.restClient().loadStories(getPeriodsAdapter().getStoriesManager().getRequestData(calendar), (Story.WrapList list) -> {
                getPeriodsAdapter().onNewStoriesFetched(list, calendar);
                refreshLayout.setRefreshing(false);
            }, (errorMessage, type) -> {
                showError(errorMessage);
                refreshLayout.setRefreshing(false);
            });
        }
    };

    private final TabBar.Actions tabBarActions = new TabBar.Actions() {
        @Override
        public void updatesClicked() {

        }

        @Override
        public void messagesClicked() {

        }

        @Override
        public void postClicked() {
            Intent intent = PostStoryActivity.newInstance();
            startActivity(intent);
        }

        @Override
        public void profileClicked() {
            Intent intent = ProfileActivity.newInstance();
            startActivity(intent);
        }
    };

    @Nullable
    @Override
    protected Serializable getSavedState() {
        getPeriodsAdapter().getStoriesManager().getSavedData((store, positions, requestData) -> {
            state.savedFetchedPositions = positions;
            state.savedStore = store;
            state.savedRequestData = requestData;
        });
        state.savedItemType = getPeriodsAdapter().getItemType();
        state.currentPosition = getHorizontalRecyclerViewLayoutManager().findFirstCompletelyVisibleItemPosition();
        return state;
    }

    private static class SavedState implements Serializable {
        private static final long serialVersionUID = 5045141075880217652L;

        boolean continueAnimation;
        boolean choosePeriodIsVisible;

        LinkedHashMap<Calendar, Story.WrapList> savedStore;
        List<Integer> savedFetchedPositions;
        int currentPosition = ArrayUtils.EMPTY_POSITION;
        StoriesManager.RequestData savedRequestData;
        PeriodsAdapter.ItemType savedItemType = PeriodsAdapter.ItemType.Large;
    }
}
