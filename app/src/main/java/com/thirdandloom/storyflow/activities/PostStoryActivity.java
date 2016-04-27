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

public class PostStoryActivity extends EmojiKeyboardActivity {

    private PostStoryBar postStoryBar;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private View keyboardReplacerView;
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
        editText = (OpenEventDetectorEditText)findViewById(R.id.activity_post_story_edit_text);
        emojiContainerView = findViewById(R.id.activity_post_story_emoji_container);
    }

    private void initGui() {
        initializeEmoji();
        keyboardController = new KeyboardController(editText, keyboardReplacerView);
        keyboardController.setKeyboardStateUpdater(this::updatePostStoryBarIcons);
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
    protected int getEmojiContainerId() {
        return R.id.activity_post_story_emoji_container;
    }

    @Override
    public void onBackPressed() {
        keyboardController.handleBackPressed(super::onBackPressed);
    }

    private void updatePostStoryBarIcons(KeyboardController.Keyboard keyboardType) {
        switch (keyboardType) {
            case Emoji:
                showEmoji();
                postStoryBar.onEmojiSelected();
                break;

            case Cats:
                hideEmoji(keyboardController.getKeyboardHeight());
                postStoryBar.onCastSelected();
                break;

            case Native:
                hideEmoji(keyboardController.getKeyboardHeight());
                postStoryBar.onNativeKeyboardSelected();
                break;

            case None:
                postStoryBar.onNoneSelected();
                break;

            default:
                throw new UnsupportedOperationException("KeyboardController.Keyboard unsupported type is using");
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

        @Override
        public void onKeyboardClicked() {
            keyboardController.keyboardClicked();
        }

        @Override
        public void onCatsClicked() {
            keyboardController.catsClicked();
        }
    };
}
