package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.adapters.PeriodsAdapter;
import com.thirdandloom.storyflow.fragments.StoryDetailsFragment;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AnimationUtils;
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
import java.util.List;

public class BrowseStoriesActivity extends BaseActivity implements StoryDetailsFragment.IStoryDetailFragmentDataSource {
    private SavedState state;

    private SnappyRecyclerView horizontalRecyclerView;
    private View periodChooserView;
    private TabBar tabBar;
    private Action1<Float> takeScrollValue;
    private StoryDetailsFragment storyDetailsFragment;
    private int recyclerViewScrollState =  RecyclerView.SCROLL_STATE_IDLE;

    public static Intent newInstance(boolean continueAnimation) {
        Intent intent = new Intent(StoryflowApplication.getInstance(), BrowseStoriesActivity.class);
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
        horizontalRecyclerView = (SnappyRecyclerView)findViewById(R.id.activity_browse_stories_horizontal_recycler_view);
        periodChooserView = findViewById(R.id.activity_browse_stories_period_chooser);
        tabBar = (TabBar)findViewById(R.id.activity_browse_stories_tab_bar);
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
            onPeriodChanged(PeriodsAdapter.ItemType.Yearly);
        });
        periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_monthly).setOnClickListener(v -> {
            getPeriodsAdapter().getStoriesManager().getRequestData().selectPeriodMonthly();
            onChangePeriodClicked();
            onPeriodChanged(PeriodsAdapter.ItemType.Monthly);
        });
        periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_daily).setOnClickListener(v -> {
            getPeriodsAdapter().getStoriesManager().getRequestData().selectPeriodDaily();
            onChangePeriodClicked();
            onPeriodChanged(PeriodsAdapter.ItemType.Daily);
        });
    }

    private void initHorizontalRecyclerView() {
        SnappyLinearLayoutManager layoutManager = new SnappyLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontalRecyclerView.setItemAnimator(new DefaultItemAnimator());
        horizontalRecyclerView.setHasFixedSize(true);
        horizontalRecyclerView.setLayoutManager(layoutManager);

        PeriodsAdapter adapter = new PeriodsAdapter(this);
        adapter.setStoryPreviewActions(storyPreviewActions);

        horizontalRecyclerView.setAdapter(adapter);
        int centerPosition = adapter.getItemCount() / 2;
        updateOffset(centerPosition);
        adapter.setCenterPosition(centerPosition);
        adapter.setItemType(PeriodsAdapter.ItemType.Daily);
        loadStoriesBetweenPositions(centerPosition - 1, centerPosition + 1);

        horizontalRecyclerView.addOnScrollListener(tabBar.getRecyclerViewScrollListener());
        horizontalRecyclerView.addOnScrollListener(new OnScrollListener());
        tabBar.setItemWidth(adapter.getItemWidthPixel());
    }

    private void onPeriodChanged(PeriodsAdapter.ItemType itemType) {
        if (getPeriodsAdapter().getItemType() != itemType) {
            int position = RecyclerLayoutManagerUtils.getCurrentVisiblePosition((LinearLayoutManager) horizontalRecyclerView.getLayoutManager());
            getPeriodsAdapter().setCenterPosition(position);
            getPeriodsAdapter().setItemType(itemType);
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
        ((BrowseStoriesToolBar)getToolbar()).onNewItemWidthSelected(adapter.getItemWidth());
    }

    private void updateOffset(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) horizontalRecyclerView.getLayoutManager();
        PeriodsAdapter adapter = getPeriodsAdapter();

        int offset = DeviceUtils.getDisplayWidth() - adapter.getItemWidthPixel() - PeriodsAdapter.getItemMargin()*2;
        layoutManager.scrollToPositionWithOffset(position, offset / 2);
    }

    private DisableScrollLinearLayoutManager getHorizontalRecyclerViewLayoutManager() {
        return (DisableScrollLinearLayoutManager) horizontalRecyclerView.getLayoutManager();
    }

    private PeriodsAdapter getPeriodsAdapter() {
        return (PeriodsAdapter)horizontalRecyclerView.getAdapter();
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
        BrowseStoriesToolBar toolBar = (BrowseStoriesToolBar)getToolbar();
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
        int centerPosition =  getPeriodsAdapter().getCenterPosition();
        PeriodsAdapter.ItemType itemType = getPeriodsAdapter().getItemType();
        StoriesManager storiesManager = getPeriodsAdapter().getStoriesManager();
        for (int position = firstVisiblePosition; position <= lastVisiblePosition; position++) {
            sendFetchStoriesRequest(position, centerPosition, itemType, storiesManager);
        }
    }

    private void sendFetchStoriesRequest(int position, int centerPosition,  PeriodsAdapter.ItemType itemType,  StoriesManager storiesManager) {
        if (!storiesManager.isFetchedPosition(position)) {
            Calendar calendar = PeriodsAdapter.getDateCalendar(position, centerPosition, itemType);
            storiesManager.addFetchedStoryPosition(position);
            StoryflowApplication.restClient().loadStories(storiesManager.getRequestData(calendar), (Story.WrapList list) -> {
                getPeriodsAdapter().onNewStoriesFetched(list, calendar);
            }, errorMessage -> {
                showError(errorMessage);
                getPeriodsAdapter().onNewStoriesFetchFailed(position);
            });
        }
    }

    private boolean canUpdateData() {
        return recyclerViewScrollState != RecyclerView.SCROLL_STATE_SETTLING;
    }

    private PeriodsAdapter.StoryHolder.Actions storyPreviewActions = new PeriodsAdapter.StoryHolder.Actions() {
        @Override
        public void onDragStarted() {
            getHorizontalRecyclerViewLayoutManager().setDisableScroll(true);
        }

        @Override
        public void onDragFinished(int velocity) {
            if (storyDetailsFragment != null) storyDetailsFragment.onDragFinished(velocity);
            getHorizontalRecyclerViewLayoutManager().setDisableScroll(false);
        }

        @Override
        public void pullToRefreshMotionNotifier(int motionEventAction) {
            getHorizontalRecyclerViewLayoutManager().setDisableScroll(motionEventAction == MotionEvent.ACTION_MOVE);
        }

        @Override
        public void onDrag(float scrollAbsolute, float scrollDelta, View scrollingView) {
            if (takeScrollValue != null) takeScrollValue.call(scrollDelta);
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
            }, errorMessage -> {
                showError(errorMessage);
                refreshLayout.setRefreshing(false);
            });
        }
    };

    @Nullable
    @Override
    protected Serializable getSavedState() {
        return state;
    }

    private static class SavedState implements Serializable {
        private static final long serialVersionUID = 5045141075880217652L;

        boolean continueAnimation;
        boolean choosePeriodIsVisible;
    }
}
