package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.AnimationUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class BrowseStoriesActivity extends BaseActivity {

    public static Intent newInstance() {
        return new Intent(StoryflowApplication.getInstance(), BrowseStoriesActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_stories);

        initGui();
        initLaunchAnimation(savedInstanceState);
    }

    private void initGui() {

    }

    @Override
    protected int getStatusBarColor() {
        return R.color.greyLighter;
    }

    @Override
    protected boolean hasToolBar() {
        return true;
    }

    private void initLaunchAnimation(Bundle savedInstanceState) {
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
}
