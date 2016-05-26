package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.adapters.MultiImageStoriesPreviewStoriesAdapter;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.views.recyclerview.OneSnapLinearLayoutManager;
import com.thirdandloom.storyflow.views.recyclerview.SnappyRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.io.Serializable;
import java.util.List;

public class MultiImageStoryPreviewActivity extends BaseActivity {

    private SnappyRecyclerView snappyRecyclerView;

    public static Intent newInstance(Story initialStory, int position, List<Story> stories, View fromView) {
        Intent intent = new Intent(StoryflowApplication.applicationContext, MultiImageStoryPreviewActivity.class);
        SavedState state = new SavedState();
        state.initialStory = initialStory;
        state.initialPosition = position;
        state.initialStoriesList = stories;
        ViewUtils.getLocationInWindow(fromView, (x, y) -> {
            state.fromViewX = x;
            state.fromViewY = y;
            state.fromViewWidth = fromView.getWidth();
            state.fromViewHeight = fromView.getHeight();
        });

        putExtra(intent, state);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_stories);
        restoreState(SavedState.class, savedInstanceState,
                restored -> state = restored,
                inited -> state = inited);
        findViews();
        initGui(savedInstanceState == null);
    }

    private void findViews() {
        snappyRecyclerView = (SnappyRecyclerView) findViewById(R.id.activity_preview_stories_snappy_recycler_view);
    }

    private void initGui(boolean firstStart) {
        initRecyclerView(firstStart);
    }

    private void initRecyclerView(boolean firstStart) {
        OneSnapLinearLayoutManager layoutManager = new OneSnapLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        snappyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        snappyRecyclerView.setHasFixedSize(true);
        snappyRecyclerView.setLayoutManager(layoutManager);

        MultiImageStoriesPreviewStoriesAdapter adapter = new MultiImageStoriesPreviewStoriesAdapter(this, state.initialStory);
        if (firstStart) {
            adapter.setThumbnailData(state.fromViewX, state.fromViewY, state.fromViewWidth, state.fromViewHeight);
        }
        snappyRecyclerView.setAdapter(adapter);
        StoryflowApplication.runOnUIThread(() -> {
            //just for test
            Timber.d("add all stories inside PreviewStoriesAdapter");
            adapter.addStories(state.initialStoriesList.subList(0, state.initialPosition), 0);
            adapter.addStories(state.initialStoriesList.subList(state.initialPosition+1, state.initialStoriesList.size()), state.initialPosition+1);
        }, 2000);
    }

    @Override
    public int getStatusBarColorResourceId() {
        return R.color.black;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Nullable
    @Override
    protected Serializable getSavedState() {
        return state;
    }

    private SavedState state;
    private static class SavedState implements Serializable {
        private static final long serialVersionUID = -8795943301848300317L;

        public Story initialStory;
        public int initialPosition;
        public List<Story> initialStoriesList;
        public int fromViewX;
        public int fromViewY;
        public int fromViewWidth;
        public int fromViewHeight;
    }
}
