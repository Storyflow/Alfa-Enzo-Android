package com.thirdandloom.storyflow.views;

import com.thirdandloom.storyflow.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class PostStoryBar extends LinearLayout {

    public interface Actions {
        void onPostStoryClicked();
        void onCameraClicked();
        void onGalleryClicked();
    }

    public PostStoryBar(Context context) {
        this(context, null);
    }

    public PostStoryBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PostStoryBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Actions actions;

    private void init() {
        inflate(getContext(), R.layout.view_post_story_bar, this);
        int padding = getResources().getDimensionPixelSize(R.dimen.sizeXXLarge);
        int paddingBottom = getResources().getDimensionPixelSize(R.dimen.sizeNormal);
        setPadding(padding, 0, padding, paddingBottom);
        setOrientation(VERTICAL);

        findViewById(R.id.view_post_story_bar_post).setOnClickListener(v -> {
            actions.onPostStoryClicked();
        });
        findViewById(R.id.view_post_story_bar_camera).setOnClickListener(v -> {
            actions.onCameraClicked();
        });
        findViewById(R.id.view_post_story_bar_gallery).setOnClickListener(v -> {
            actions.onGalleryClicked();
        });
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }
}
