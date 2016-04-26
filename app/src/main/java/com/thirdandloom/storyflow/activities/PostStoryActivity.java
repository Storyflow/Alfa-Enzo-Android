package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.views.OpenEventDetectorEditText;
import com.thirdandloom.storyflow.views.PostStoryBar;
import com.thirdandloom.storyflow.views.SizeNotifierFrameLayout;
import com.thirdandloom.storyflow.views.emoji.KeyboardController;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class PostStoryActivity extends BaseActivity {

    private PostStoryBar postStoryBar;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private View keyboardReplacerView;
    private OpenEventDetectorEditText postStoryEditText;
    private KeyboardController keyboardController;

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

        keyboardController.openKeyboardInternal();
    }

    private void findViews() {
        postStoryBar = (PostStoryBar)findViewById(R.id.activity_post_story_post_bar);
        sizeNotifierLayout = (SizeNotifierFrameLayout)findViewById(R.id.activity_post_story_size_notifier);
        keyboardReplacerView = findViewById(R.id.activity_post_story_keyboard_replacer);
        postStoryEditText = (OpenEventDetectorEditText)findViewById(R.id.activity_post_story_edit_text);
    }

    private void initGui() {
        keyboardController = new KeyboardController(postStoryEditText, keyboardReplacerView);
        keyboardController.setEmojiPopupVisibilityUpdater(this::updatePostStoryBarIcons);
        postStoryBar.setActions(postStoryActions);
        sizeNotifierLayout.setActions(keyboardController);
    }

    @Override
    protected int getStatusBarColorResourceId() {
        return R.color.greyXLighter;
    }

    @Override
    protected boolean hasToolBar() {
        return true;
    }

    @Override
    public void onBackPressed() {
        keyboardController.handleBackPressed(super::onBackPressed);
    }

    private void updatePostStoryBarIcons(boolean emojiPopupIsVisible) {
        if (emojiPopupIsVisible) {
            postStoryBar.emojiDidSelect();
        } else {
            postStoryBar.keyboardDidSelect();
        }
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

        @Override
        public void onEmojiClicked() {
            keyboardController.onEmojiClicked();
        }
    };
}
