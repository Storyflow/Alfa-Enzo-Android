package com.thirdandloom.storyflow.activities;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.ActivityUtils;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.image.PhotoFileUtils;
import com.thirdandloom.storyflow.views.edittext.OpenEventDetectorEditText;
import com.thirdandloom.storyflow.views.PostStoryBar;
import com.thirdandloom.storyflow.views.SizeNotifierFrameLayout;
import com.thirdandloom.storyflow.views.emoji.CatsStickersView;
import com.thirdandloom.storyflow.views.emoji.KeyboardController;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import java.io.Serializable;

public class PostStoryActivity extends EmojiKeyboardActivity {
    private static final int CAPTURE_PHOTO = 1;
    private static final int SELECT_PHOTO = CAPTURE_PHOTO + 1;

    private PostStoryBar postStoryBar;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private View keyboardReplacerView;
    private KeyboardController keyboardController;
    private EditText postStoryEditText;
    private ScrollView scrollViewContainer;
    private int defaultScrollViewHeight;
    private SavedState state = new SavedState();

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
        restoreState(savedInstanceState, restoredState -> {
            state = (SavedState) restoredState;
        });
        ViewUtils.callOnPreDraw(scrollViewContainer, view -> {
            defaultScrollViewHeight = view.getHeight();
        });
        //does not work ;(
        AndroidUtils.showKeyboard(postStoryEditText);
    }

    private void findViews() {
        postStoryBar = (PostStoryBar)findViewById(R.id.activity_post_story_post_bar);
        sizeNotifierLayout = (SizeNotifierFrameLayout)findViewById(R.id.activity_post_story_size_notifier);
        keyboardReplacerView = findViewById(R.id.activity_post_story_keyboard_replacer);
        editText = (OpenEventDetectorEditText)findViewById(R.id.activity_post_story_edit_text);
        emojiContainerView = findViewById(R.id.activity_post_story_emoji_container);
        catsStickersView = (CatsStickersView)findViewById(R.id.activity_post_story_cats_emoji);
        scrollViewContainer = (ScrollView)findViewById(R.id.activity_post_story_scroll_view);

        postStoryEditText = editText;
    }

    private void initGui() {
        initializeEmoji();
        keyboardController = new KeyboardController(editText, keyboardReplacerView);
        keyboardController.setKeyboardStateUpdater(this::updatePostStoryBarIcons);
        postStoryBar.setActions(postStoryActions);
        sizeNotifierLayout.setActions(keyboardController);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PhotoFileUtils.REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCapturePhotoIntent();
                } else {
                    showWarning(R.string.permissions_were_not_guaranteed);
                }
                break;
        }
    }

    private void startCapturePhotoIntent() {
        state.capturedAbsolutePhotoPath = ActivityUtils.capturePhoto(this, CAPTURE_PHOTO);
    }

    private void selectPhoto() {
        ActivityUtils.selectPhoto(this, SELECT_PHOTO);
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
        int newScrollViewHeight;
        switch (keyboardType) {
            case Emoji:
                showEmoji();
                hideCatsEmoji(keyboardController.getKeyboardHeight());
                postStoryBar.onEmojiSelected();

                newScrollViewHeight = defaultScrollViewHeight - keyboardController.getKeyboardHeight();
                break;

            case Cats:
                showCatsEmoji();
                hideEmoji(keyboardController.getKeyboardHeight());
                postStoryBar.onCastSelected();

                newScrollViewHeight = defaultScrollViewHeight - keyboardController.getKeyboardHeight();
                break;

            case Native:
                hideEmoji(keyboardController.getKeyboardHeight());
                hideCatsEmoji(keyboardController.getKeyboardHeight());
                postStoryBar.onNativeKeyboardSelected();
                newScrollViewHeight = defaultScrollViewHeight - keyboardController.getKeyboardHeight();
                break;

            case None:
                postStoryBar.onNoneSelected();

                newScrollViewHeight = defaultScrollViewHeight;
                break;

            default:
                throw new UnsupportedOperationException("KeyboardController.Keyboard unsupported type is using");
        }
        int newEditTextHeight = newScrollViewHeight - postStoryBar.getHeight();
        ViewUtils.applyHeight(postStoryEditText, newEditTextHeight);
        ViewUtils.applyHeight(scrollViewContainer, newScrollViewHeight);
    }

    private final PostStoryBar.Actions postStoryActions = new PostStoryBar.Actions() {
        @Override
        public void onPostStoryClicked() {
            Timber.d("PostStoryBar Actions onPostStoryClicked");
        }

        @Override
        public void onCameraClicked() {
            PhotoFileUtils.checkStoragePermissionsAreGuaranteed(PostStoryActivity.this, PostStoryActivity.this::startCapturePhotoIntent);
        }

        @Override
        public void onGalleryClicked() {
            PhotoFileUtils.checkStoragePermissionsAreGuaranteed(PostStoryActivity.this, PostStoryActivity.this::selectPhoto);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String selectedPhoto = "";
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAPTURE_PHOTO:
                    selectedPhoto = state.capturedAbsolutePhotoPath;
                    Glide
                            .with(this)
                            .load(selectedPhoto)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                    editText.setCo;
                                }
                            });
                    break;
                case SELECT_PHOTO:
                    selectedPhoto = data.getData().toString();
                    break;
            }
        }
        Timber.d("selected photo: %s", selectedPhoto);
    }

    @Nullable
    @Override
    protected Serializable getSavedState() {
        return state;
    }

    private static class SavedState implements Serializable {
        private static final long serialVersionUID = 8645864584763024484L;
        String capturedAbsolutePhotoPath;
    }
}
