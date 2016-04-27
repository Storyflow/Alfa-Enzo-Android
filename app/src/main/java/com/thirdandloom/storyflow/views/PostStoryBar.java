package com.thirdandloom.storyflow.views;

import com.thirdandloom.storyflow.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class PostStoryBar extends LinearLayout {

    public interface Actions {
        void onPostStoryClicked();
        void onCameraClicked();
        void onGalleryClicked();
        void onEmojiClicked();
        void onKeyboardClicked();
        void onCatsClicked();
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
    private TextView emojiTextView;
    private TextView nativeKeyboardTextView;
    private TextView catsTextView;

    private List<View> keyboardViews;

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
        emojiTextView = (TextView)findViewById(R.id.view_post_story_bar_emoji);
        emojiTextView.setOnClickListener(v -> {
            actions.onEmojiClicked();
        });
        catsTextView = (TextView)findViewById(R.id.view_post_story_bar_cats);
        catsTextView.setOnClickListener(v -> {
            actions.onCatsClicked();
        });
        nativeKeyboardTextView = (TextView)findViewById(R.id.view_post_story_bar_keyboard);
        nativeKeyboardTextView.setOnClickListener(v -> {
            actions.onKeyboardClicked();
        });
        keyboardViews = Arrays.asList(nativeKeyboardTextView, emojiTextView, catsTextView);
        disableAllKeyboardViews();
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public void onEmojiSelected() {
        disableAllKeyboardViews();
        emojiTextView.setSelected(true);
    }

    public void onCastSelected() {
        disableAllKeyboardViews();
        catsTextView.setSelected(true);
    }

    public void onNativeKeyboardSelected() {
        disableAllKeyboardViews();
        nativeKeyboardTextView.setSelected(true);
    }

    public void onNoneSelected() {
        disableAllKeyboardViews();
    }

    private void disableAllKeyboardViews() {
        for (View view : keyboardViews) {
            view.setSelected(false);
        }
    }
}
