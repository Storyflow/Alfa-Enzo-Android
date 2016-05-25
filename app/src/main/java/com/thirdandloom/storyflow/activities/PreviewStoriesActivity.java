package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.Serializable;

public class PreviewStoriesActivity extends BaseActivity {

    public static Intent newInstance() {
        return new Intent(StoryflowApplication.applicationContext, PreviewStoriesActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_stories);
        state = (SavedState) getState();
        restoreState(savedInstanceState, restoredState -> {
            state = (SavedState) restoredState;
        });
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
    }
}
