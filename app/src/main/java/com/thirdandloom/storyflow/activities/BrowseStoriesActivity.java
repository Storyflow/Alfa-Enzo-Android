package com.thirdandloom.storyflow.activities;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.adapters.HorizontalRecyclerViewAdapter;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.utils.AnimationUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.glide.CropCircleTransformation;
import com.thirdandloom.storyflow.views.recyclerview.SnappyLinearLayoutManager;
import com.thirdandloom.storyflow.views.recyclerview.SnappyRecyclerView;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BrowseStoriesActivity extends BaseActivity {
    private SavedState state;
    private StoriesManager storiesManager = new StoriesManager();

    private SnappyRecyclerView horizontalRecyclerView;
    private View periodChooserView;

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
    }

    private void initGui() {
        initToolBar();
        initHorizontalRecyclerView();
        initPeriodChooser();
    }

    private void initPeriodChooser() {
        periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_yearly).setOnClickListener(v -> {
            onChangePeriodClicked();
        });
        periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_monthly).setOnClickListener(v -> {
            onChangePeriodClicked();
        });
        periodChooserView.findViewById(R.id.activity_browse_stories_period_chooser_daily).setOnClickListener(v -> {
            onChangePeriodClicked();
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

        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter();
        horizontalRecyclerView.setAdapter(adapter);
        updateOffset(adapter.getItemCount() / 2);
    }

    private void onLoadedStories(Story.WrapList stories) {
        Timber.d("stories" + stories.toString());
        Timber.d("stories 123");
        Timber.d("stories 312");
        Timber.d("stories 214");
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
        HorizontalRecyclerViewAdapter adapter = (HorizontalRecyclerViewAdapter)horizontalRecyclerView.getAdapter();
        adapter.changeItemWidth();
        adapter.notifyDataSetChanged();

        LinearLayoutManager layoutManager = (LinearLayoutManager) horizontalRecyclerView.getLayoutManager();
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        int position = lastVisiblePosition - firstVisiblePosition == 0
                ? firstVisiblePosition
                : firstVisiblePosition + 1;
        updateOffset(position);

        ImageView imageView = (ImageView) view;
        imageView.setImageResource(toolbarSizeImage.get(adapter.getItemWidth().ordinal()));
    }

    private void updateOffset(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) horizontalRecyclerView.getLayoutManager();
        HorizontalRecyclerViewAdapter adapter = (HorizontalRecyclerViewAdapter)horizontalRecyclerView.getAdapter();

        int offset = DeviceUtils.getDisplayWidth() - adapter.getItemWidthPixel() - HorizontalRecyclerViewAdapter.getItemMarging()*2;
        layoutManager.scrollToPositionWithOffset(position, offset/2);
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

    static final Map<Integer, Integer> toolbarSizeImage;
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
