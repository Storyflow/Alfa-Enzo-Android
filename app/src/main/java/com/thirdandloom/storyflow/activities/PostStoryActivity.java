package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.views.PostStoryBar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class PostStoryActivity extends BaseActivity {

    private PostStoryBar postStoryBar;

    public static Intent newInstance() {
        return new Intent(StoryflowApplication.getInstance(), PostStoryActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_story);
        setTitle(R.string.post_story);
        findViews();
        initGui();
    }

    private void findViews() {
        postStoryBar = (PostStoryBar)findViewById(R.id.activity_post_story_post_bar);
    }

    private void initGui() {
        postStoryBar.setActions(postStoryActions);
    }

    @Override
    protected int getStatusBarColorResourceId() {
        return R.color.greyXLighter;
    }

    @Override
    protected boolean hasToolBar() {
        return true;
    }

    private final PostStoryBar.Actions postStoryActions = new PostStoryBar.Actions() {
        @Override
        public void onPostStoryClicked() {
            Timber.d("PostStoryBar Actions onPostStoryClicked");
        }

        @Override
        public void onCameraClicked() {
            Timber.d("PostStoryBar Actions onCameraClicked");
        }

        @Override
        public void onGalleryClicked() {
            Timber.d("PostStoryBar Actions onGalleryClicked");
        }
    };
}
