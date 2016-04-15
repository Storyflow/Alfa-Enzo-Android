package com.thirdandloom.storyflow.activities;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.adapters.HorizontalRecyclerViewAdapter;
import com.thirdandloom.storyflow.fragments.StoryDetailsFragment;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.utils.AnimationUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.RecyclerLayoutManagerUtils;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.glide.CropCircleTransformation;
import com.thirdandloom.storyflow.views.TabBar;
import com.thirdandloom.storyflow.views.recyclerview.DisableScrollLinearLayoutManager;
import com.thirdandloom.storyflow.views.recyclerview.SnappyLinearLayoutManager;
import com.thirdandloom.storyflow.views.recyclerview.SnappyRecyclerView;
import rx.functions.Action1;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowseStoriesActivity extends BaseActivity implements StoryDetailsFragment.IStoryDetailFragmentDataSource {
    private SavedState state;
    private StoriesManager storiesManager = new StoriesManager();

    private SnappyRecyclerView horizontalRecyclerView;
    private View periodChooserView;
    private TabBar tabBar;
    private Action1<Float> takeScrollValue;
    private StoryDetailsFragment storyDetailsFragment;

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

        //StoryflowApplication.restClient().loadStories(storiesManager.getRequestData(), this::onLoadedStories, errorMessage -> {
        //});
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
        periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_yearly).setOnClickListener(v -> {
            onChangePeriodClicked();
            onPeriodChanged(HorizontalRecyclerViewAdapter.ItemType.Yearly);
        });
        periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_monthly).setOnClickListener(v -> {
            onChangePeriodClicked();
            onPeriodChanged(HorizontalRecyclerViewAdapter.ItemType.Monthly);
        });
        periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_daily).setOnClickListener(v -> {
            onChangePeriodClicked();
            onPeriodChanged(HorizontalRecyclerViewAdapter.ItemType.Daily);
        });
        periodChooserView.getLayoutParams().height = 1;
        periodChooserView.requestLayout();
    }

    private void initHorizontalRecyclerView() {
        SnappyLinearLayoutManager layoutManager = new SnappyLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontalRecyclerView.setItemAnimator(new DefaultItemAnimator());
        horizontalRecyclerView.setHasFixedSize(true);
        horizontalRecyclerView.setLayoutManager(layoutManager);

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(this);
        adapter.setPullToRefreshNotifier(this::onPullToRefresh);
        adapter.setOnChildDragFinished(this::onVerticalScrollFinished);
        adapter.setOnChildDragStarted(this::onVerticalScrollStarted);
        adapter.setOnChildDrag(this::onVerticalScroll);
        adapter.setOnChildClick(this::onClickStory);

        horizontalRecyclerView.setAdapter(adapter);
        int centerPosition = adapter.getItemCount() / 2;
        adapter.setCenterPosition(centerPosition);
        adapter.setItemType(HorizontalRecyclerViewAdapter.ItemType.Daily);
        updateOffset(centerPosition);

        horizontalRecyclerView.addOnScrollListener(tabBar.new OnScrollListener());
        tabBar.setItemWidth(adapter.getItemWidthPixel() + HorizontalRecyclerViewAdapter.getItemMargin() * 2);
    }

    private void onLoadedStories(Story.WrapList stories) {
        Timber.d("stories" + stories.toString());
        Timber.d("stories 123");
        Timber.d("stories 312");
        Timber.d("stories 214");
    }

    private void onPeriodChanged(HorizontalRecyclerViewAdapter.ItemType itemType) {
        int position = RecyclerLayoutManagerUtils.getCurrentVisiblePosition((LinearLayoutManager) horizontalRecyclerView.getLayoutManager());
        getHorizontalAdapter().setCenterPosition(position);
        getHorizontalAdapter().setItemType(itemType);
        getHorizontalAdapter().notifyDataSetChanged();
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

    private void onChangeSizeClicked(View view) {
        HorizontalRecyclerViewAdapter adapter = getHorizontalAdapter();
        adapter.changeItemWidth();
        adapter.notifyDataSetChanged();

        int position = RecyclerLayoutManagerUtils.getCurrentVisiblePosition((LinearLayoutManager) horizontalRecyclerView.getLayoutManager());
        updateOffset(position);

        ImageView imageView = (ImageView) view;
        imageView.setImageResource(toolbarSizeImage.get(adapter.getItemWidth().ordinal()));
        tabBar.setItemWidth(adapter.getItemWidthPixel() + HorizontalRecyclerViewAdapter.getItemMargin() * 2);
    }

    private void updateOffset(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) horizontalRecyclerView.getLayoutManager();
        HorizontalRecyclerViewAdapter adapter = getHorizontalAdapter();

        int offset = DeviceUtils.getDisplayWidth() - adapter.getItemWidthPixel() - HorizontalRecyclerViewAdapter.getItemMargin()*2;
        layoutManager.scrollToPositionWithOffset(position, offset / 2);
    }

    private void onClickStory(View fromView) {
        storyDetailsFragment = StoryDetailsFragment.newInstance(fromView, true);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activity_browse_stories_container, storyDetailsFragment, StoryDetailsFragment.class.getSimpleName());
        ft.commit();
    }

    private void onVerticalScrollFinished(int velocity) {
        storyDetailsFragment.onDragFinished(velocity);
        getHorizontalRecyclerViewLayoutManager().setDisableScroll(false);
    }

    private void onVerticalScrollStarted() {
        getHorizontalRecyclerViewLayoutManager().setDisableScroll(true);
    }

    private void onPullToRefresh(Integer action) {
        getHorizontalRecyclerViewLayoutManager().setDisableScroll(action == MotionEvent.ACTION_MOVE);
    }

    private void onVerticalScroll(Float delta, Float dy, View fromView) {
        if (takeScrollValue != null) takeScrollValue.call(dy);
        if (delta > 0) {
            if (storyDetailsFragment == null || !storyDetailsFragment.isAdded()) {
                storyDetailsFragment = StoryDetailsFragment.newInstance(fromView, false);
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

    private DisableScrollLinearLayoutManager getHorizontalRecyclerViewLayoutManager() {
        return (DisableScrollLinearLayoutManager) horizontalRecyclerView.getLayoutManager();
    }

    private HorizontalRecyclerViewAdapter getHorizontalAdapter() {
        return (HorizontalRecyclerViewAdapter)horizontalRecyclerView.getAdapter();
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.greyLighter;
    }

    @Override
    protected boolean hasToolBar() {
        return true;
    }

    private void initToolBar() {
        View view = getLayoutInflater().inflate(R.layout.toolbar_activity_browsing_stories, getToolbar(), true);
        TextView userName = (TextView) view.findViewById(R.id.toolbar_activity_browsing_stories_user_name);
        TextView fullUserName = (TextView) view.findViewById(R.id.toolbar_activity_browsing_stories_full_name);
        ImageView avatar = (ImageView) view.findViewById(R.id.toolbar_activity_browsing_stories_avatar);
        User user = StoryflowApplication.account().getUser();
        userName.setText(user.getUsername());
        fullUserName.setText(user.getFullUserName());
        view.findViewById(R.id.toolbar_activity_browsing_stories_change_period).setOnClickListener(v -> onChangePeriodClicked());
        view.findViewById(R.id.toolbar_activity_browsing_stories_increase_size).setOnClickListener(this::onChangeSizeClicked);
        Glide
                .with(this)
                .load(user.getProfileImage().getImageUrl())
                .bitmapTransform(new CropCircleTransformation(this))
                .dontAnimate()
                .into(avatar);
    }

    private static final Map<Integer, Integer> toolbarSizeImage;
    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(HorizontalRecyclerViewAdapter.ItemWidth.Small.ordinal(), R.drawable.plus_icon);
        map.put(HorizontalRecyclerViewAdapter.ItemWidth.Large.ordinal(), R.drawable.minus_icon);
        toolbarSizeImage = Collections.unmodifiableMap(map);
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
