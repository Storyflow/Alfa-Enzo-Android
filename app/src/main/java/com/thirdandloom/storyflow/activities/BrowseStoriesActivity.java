package com.thirdandloom.storyflow.activities;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.utils.AnimationUtils;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.glide.CropCircleTransformation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

public class BrowseStoriesActivity extends BaseActivity {
    private SavedState state;
    private StoriesManager storiesManager = new StoriesManager();

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

        initGui();

        StoryflowApplication.restClient().loadStories(storiesManager.getRequestData(), this::onLoadedStories, errorMessage -> {
        });
    }

    private void initGui() {
        initToolBar();
    }

    private void onLoadedStories(Story.WrapList stories) {
        Timber.d("stories" + stories.toString());
        Timber.d("stories 123");
        Timber.d("stories 312");
        Timber.d("stories 214");
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
        Glide
                .with(this)
                .load(user.getProfileImage().getImageUrl())
                .bitmapTransform(new CropCircleTransformation(this))
                .dontAnimate()
                .into(avatar);
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
    }
}
