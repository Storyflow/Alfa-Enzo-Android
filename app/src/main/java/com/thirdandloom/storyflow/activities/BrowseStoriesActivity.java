package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.activities.launch.ProfileActivity;
import com.thirdandloom.storyflow.adapters.BrowsePeriodsAdapter;
import com.thirdandloom.storyflow.adapters.holder.BrowsePeriodBaseHolder;
import com.thirdandloom.storyflow.fragments.ReadingStoriesFragment;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AnimationUtils;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.RecyclerLayoutManagerUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.animations.SpringAnimation;
import com.thirdandloom.storyflow.utils.event.StoryCreationFailedEvent;
import com.thirdandloom.storyflow.utils.event.StoryCreationSuccessEvent;
import com.thirdandloom.storyflow.utils.event.StoryDeletePendingEvent;
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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

public class BrowseStoriesActivity extends BaseActivity implements ReadingStoriesFragment.IStoryDetailFragmentDataSource {
    private static final int CREATE_NEW_STORY = 1;

    public static Intent newInstance(boolean continueAnimation) {
        Intent intent = new Intent(StoryflowApplication.applicationContext, BrowseStoriesActivity.class);
        SavedState state = new SavedState();
        state.continueAnimation = continueAnimation;
        putExtra(intent, state);
        return intent;
    }

    private SnappyRecyclerView snappyRecyclerView;
    private View periodChooserView;
    private TabBar tabBar;
    private Action1<Float> takeScrollValue;
    private ReadingStoriesFragment storyDetailsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_stories);
        restoreState(SavedState.class, savedInstanceState,
                restored -> state = restored,
                inited -> state = inited);

        if (state.continueAnimation) {
            initLaunchAnimation(savedInstanceState);
        } else {
            ViewUtils.hide(findViewById(R.id.launch_layout));
        }
        findViews();
        initGui();
    }

    private void findViews() {
        snappyRecyclerView = (SnappyRecyclerView) findViewById(R.id.activity_browse_stories_horizontal_recycler_view);
        periodChooserView = findViewById(R.id.activity_browse_stories_period_chooser);
        tabBar = (TabBar) findViewById(R.id.activity_browse_stories_tab_bar);
    }

    private void initGui() {
        initToolBar();
        initSnappyRecyclerView();
        initPeriodChooser();
    }

    private void initPeriodChooser() {
        periodChooserView.getLayoutParams().height = 0;
        periodChooserView.requestLayout();
        View yearlyView = periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_yearly);
        View dailyView = periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_daily);
        View monthlyView = periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_monthly);

        yearlyView.setOnClickListener(v -> {
            getPeriodsAdapter().getStoriesManager().getRequestData().selectPeriodYearly();
            changePeriod();
            onPeriodChanged(BrowsePeriodsAdapter.PeriodType.Yearly);
        });
        monthlyView.setOnClickListener(v -> {
            getPeriodsAdapter().getStoriesManager().getRequestData().selectPeriodMonthly();
            changePeriod();
            onPeriodChanged(BrowsePeriodsAdapter.PeriodType.Monthly);
        });
        dailyView.setOnClickListener(v -> {
            getPeriodsAdapter().getStoriesManager().getRequestData().selectPeriodDaily();
            changePeriod();
            onPeriodChanged(BrowsePeriodsAdapter.PeriodType.Daily);
        });
        SpringAnimation.initVisibleAfterClick(dailyView);
        SpringAnimation.initVisibleAfterClick(monthlyView);
        SpringAnimation.initVisibleAfterClick(yearlyView);
    }

    private void initSnappyRecyclerView() {
        SnappyLinearLayoutManager layoutManager = new SnappyLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        snappyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        snappyRecyclerView.setHasFixedSize(true);
        snappyRecyclerView.setLayoutManager(layoutManager);

        BrowsePeriodsAdapter adapter = new BrowsePeriodsAdapter(this, state.savedStore, state.savedFetchedPositions, state.savedRequestData);
        adapter.setStoryPreviewActions(storyPreviewActions);
        adapter.setItemType(state.savedItemType);
        adapter.setGetParentHeightAction(() -> snappyRecyclerView.getHeight());

        snappyRecyclerView.setAdapter(adapter);
        ((BrowseStoriesToolBar) getToolbar()).onNewItemWidthSelected(adapter.getItemType());

        int centerPosition = state.currentPosition != ArrayUtils.INDEX_NOT_FOUND
                ? state.currentPosition
                : adapter.getItemCount() / 2;
        updateOffset(centerPosition);
        adapter.setCenterPosition(centerPosition);
        adapter.updatePeriodType();
        loadStoriesBetweenPositions(centerPosition - 1, centerPosition + 1);

        snappyRecyclerView.addOnScrollListener(tabBar.getRecyclerViewScrollListener());
        snappyRecyclerView.addOnScrollListener(new OnScrollListener());
        tabBar.setItemWidth(adapter.getItemWidthPixel()+ BrowsePeriodsAdapter.getItemMargin()*2);
        tabBar.setActions(tabBarActions);
    }

    private void onPeriodChanged(BrowsePeriodsAdapter.PeriodType periodType) {
        if (getPeriodsAdapter().getPeriodType() != periodType) {
            int position = RecyclerLayoutManagerUtils.getCenterVisiblePosition((LinearLayoutManager) snappyRecyclerView.getLayoutManager());
            getPeriodsAdapter().setCenterPosition(position);
            getPeriodsAdapter().setPeriodType(periodType);
            getPeriodsAdapter().clearData();
            getPeriodsAdapter().notifyDataSetChanged();
            int first = getLayoutManager().findFirstVisibleItemPosition();
            int last = getLayoutManager().findLastVisibleItemPosition();
            loadStoriesBetweenPositions(first, last);
        }
    }

    private void changePeriod() {
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

    private void changeSize() {
        BrowsePeriodsAdapter adapter = getPeriodsAdapter();
        int previousItems = DeviceUtils.getDisplayWidth()/adapter.getItemWidthPixel();
        adapter.changeItemWidth();
        adapter.notifyDataSetChanged();
        int position = RecyclerLayoutManagerUtils.getCenterVisiblePosition((LinearLayoutManager) snappyRecyclerView.getLayoutManager());
        updateOffset(position);
        tabBar.setItemWidth(adapter.getItemWidthPixel() + BrowsePeriodsAdapter.getItemMargin() * 2);
        ((BrowseStoriesToolBar) getToolbar()).onNewItemWidthSelected(adapter.getItemType());

        int newVisibleItems = DeviceUtils.getDisplayWidth()/adapter.getItemWidthPixel();
        if (newVisibleItems > previousItems) {
            int delta = newVisibleItems - previousItems;
            int first = getLayoutManager().findFirstVisibleItemPosition();
            int last = getLayoutManager().findLastVisibleItemPosition();
            fetchData(first-delta, last+delta);
        }
    }

    private void updateOffset(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) snappyRecyclerView.getLayoutManager();
        BrowsePeriodsAdapter adapter = getPeriodsAdapter();

        int offset = DeviceUtils.getDisplayWidth() - adapter.getItemWidthPixel() - BrowsePeriodsAdapter.getItemMargin() * 2;
        layoutManager.scrollToPositionWithOffset(position, offset / 2);
    }

    private DisableScrollLinearLayoutManager getLayoutManager() {
        return (DisableScrollLinearLayoutManager) snappyRecyclerView.getLayoutManager();
    }

    private BrowsePeriodsAdapter getPeriodsAdapter() {
        return (BrowsePeriodsAdapter) snappyRecyclerView.getAdapter();
    }

    @Override
    public int getStatusBarColorResourceId() {
        return R.color.greyXLighter;
    }

    @Override
    protected boolean hasToolBar() {
        return true;
    }

    private void initToolBar() {
        BrowseStoriesToolBar toolBar = (BrowseStoriesToolBar) getToolbar();
        toolBar.setActions(new BrowseStoriesToolBar.Actions() {
            @Override
            public void onChangeSizeClicked() {
                changeSize();
            }

            @Override
            public void onChangePeriodClicked() {
                changePeriod();
            }

            @Override
            public void onChangeAuthorsClicked() {

            }
        });
    }

    @Override
    public void setTakeScrollDelta(Action1<Float> takeScroll) {
        this.takeScrollValue = takeScroll;
    }

    @Override
    public View getBottomBar() {
        return tabBar;
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
            if (newState != RecyclerView.SCROLL_STATE_SETTLING) {
                int first = getLayoutManager().findFirstVisibleItemPosition();
                int last = getLayoutManager().findLastVisibleItemPosition();
                fetchData(first, last);
            }
        }
    }

    private void fetchData(int firstVisiblePosition, int lastVisiblePosition) {
        loadStoriesBetweenPositions(firstVisiblePosition, lastVisiblePosition);
    }

    private void loadStoriesBetweenPositions(int firstVisiblePosition, int lastVisiblePosition) {
        int centerPosition = getPeriodsAdapter().getCenterPosition();
        BrowsePeriodsAdapter.PeriodType periodType = getPeriodsAdapter().getPeriodType();
        StoriesManager storiesManager = getPeriodsAdapter().getStoriesManager();
        for (int position = firstVisiblePosition; position <= lastVisiblePosition; position++) {
            sendFetchStoriesRequest(position, centerPosition, periodType, storiesManager);
        }
    }

    private void sendFetchStoriesRequest(int position, int centerPosition, BrowsePeriodsAdapter.PeriodType periodType, StoriesManager storiesManager) {
        if (!storiesManager.isFetchedPosition(position)) {
            Calendar calendar = BrowsePeriodsAdapter.getDateCalendar(position, centerPosition, periodType);
            storiesManager.addFetchedStoryPosition(position);
            StoryflowApplication.restClient().loadStories(storiesManager.getRequestData(calendar), (Story.WrapList list) -> {
                getPeriodsAdapter().onNewStoriesFetched(list, calendar, position);
            }, (errorMessage, type) -> {
                showError(errorMessage);
                getPeriodsAdapter().onNewStoriesFetchFailed(position);
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StoryCreationFailedEvent event) {
        getPeriodsAdapter().notifyItemChanged(pendingStoryPosition(event.getStory()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StoryCreationSuccessEvent event) {
        PendingStory story = event.getStory();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(story.getDate());
        StoryflowApplication.restClient().loadStories(getPeriodsAdapter().getStoriesManager().getRequestData(calendar), (Story.WrapList list) -> {
            getPeriodsAdapter().onNewStoriesFetched(list, calendar);
        }, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StoryDeletePendingEvent event) {
        getPeriodsAdapter().notifyItemChanged(pendingStoryPosition(event.getStory()));
    }

    private final BrowsePeriodBaseHolder.Actions storyPreviewActions = new BrowsePeriodBaseHolder.Actions() {
        @Override
        public void onDragStarted() {
            getLayoutManager().setDisableScroll(true);
        }

        @Override
        public void onDragFinished(int velocity) {
            if (storyDetailsFragment != null)
                storyDetailsFragment.onDragFinished(velocity);
            getLayoutManager().setDisableScroll(false);
        }

        @Override
        public void pullToRefreshMotionNotifier(int motionEventAction) {
            getLayoutManager().setDisableScroll(motionEventAction == MotionEvent.ACTION_MOVE);
        }

        @Override
        public void onDrag(float scrollAbsolute, float scrollDelta, View scrollingView, Calendar calendar) {
            if (takeScrollValue != null)
                takeScrollValue.call(scrollDelta);
            if (scrollAbsolute > 0) {
                if (storyDetailsFragment == null || !storyDetailsFragment.isAdded()) {
                    storyDetailsFragment = ReadingStoriesFragment.newInstance(scrollingView, false,  getPeriodsAdapter().getStoriesManager(), calendar);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.activity_browse_stories_container, storyDetailsFragment, ReadingStoriesFragment.class.getSimpleName());
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
        public void onClick(View view, Calendar calendar) {
            storyDetailsFragment = ReadingStoriesFragment.newInstance(view, true,  getPeriodsAdapter().getStoriesManager(), calendar);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.activity_browse_stories_container, storyDetailsFragment, ReadingStoriesFragment.class.getSimpleName());
            ft.commit();
        }

        @Override
        public void onPullToRefreshStarted(SwipeRefreshLayout refreshLayout, Calendar calendar, int position) {
            StoryflowApplication.restClient().loadStories(getPeriodsAdapter().getStoriesManager().getRequestData(calendar), (Story.WrapList list) -> {
                getPeriodsAdapter().onNewStoriesFetched(list, calendar, position);
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
            startActivityForResult(intent, CREATE_NEW_STORY);
        }

        @Override
        public void profileClicked() {
            Intent intent = ProfileActivity.newInstance();
            startActivity(intent);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CREATE_NEW_STORY:
                    PendingStory story = PostStoryActivity.extractResult(data);
                    getPeriodsAdapter().notifyItemChanged(pendingStoryPosition(story));
                    break;
                default:
                    break;
            }
        }
    }

    private int pendingStoryPosition(PendingStory story) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(story.getDate());
        return getPeriodsAdapter().getPosition(calendar);
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

    @Nullable
    @Override
    protected Serializable getSavedState() {
        getPeriodsAdapter().getStoriesManager().getSavedData((store, positions, requestData) -> {
            state.savedFetchedPositions = positions;
            state.savedStore = store;
            state.savedRequestData = requestData;
        });
        state.savedItemType = getPeriodsAdapter().getItemType();
        state.currentPosition = getLayoutManager().findFirstCompletelyVisibleItemPosition();
        return state;
    }

    private SavedState state;
    private static class SavedState implements Serializable {
        private static final long serialVersionUID = 5045141075880217652L;

        boolean continueAnimation;
        boolean choosePeriodIsVisible;

        LinkedHashMap<Calendar, Story.WrapList> savedStore;
        List<Integer> savedFetchedPositions;
        int currentPosition = ArrayUtils.EMPTY_POSITION;
        StoriesManager.RequestData savedRequestData;
        BrowsePeriodsAdapter.ItemType savedItemType = BrowsePeriodsAdapter.ItemType.Large;
    }
}
