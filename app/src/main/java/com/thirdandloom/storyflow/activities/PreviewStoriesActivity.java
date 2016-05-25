package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.adapters.PreviewStoriesAdapter;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.views.recyclerview.OneSnapLinearLayoutManager;
import com.thirdandloom.storyflow.views.recyclerview.SnappyRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import java.io.Serializable;
import java.util.List;

public class PreviewStoriesActivity extends BaseActivity {

    private SnappyRecyclerView snappyRecyclerView;

    public static Intent newInstance(Story initialStory, int position, List<Story> stories) {
        Intent intent = new Intent(StoryflowApplication.applicationContext, PreviewStoriesActivity.class);
        SavedState state = new SavedState();
        state.initialStory = initialStory;
        state.initialPosition = position;
        state.initialStoriesList = stories;
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
        initGui();
    }

    private void findViews() {
        snappyRecyclerView = (SnappyRecyclerView) findViewById(R.id.activity_preview_stories_snappy_recycler_view);
    }


    private void initGui() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        OneSnapLinearLayoutManager layoutManager = new OneSnapLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        snappyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        snappyRecyclerView.setHasFixedSize(true);
        snappyRecyclerView.setLayoutManager(layoutManager);

        PreviewStoriesAdapter adapter = new PreviewStoriesAdapter(this, state.initialStory);
        snappyRecyclerView.setAdapter(adapter);
        StoryflowApplication.runOnUIThread(() -> {
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
    }
}
