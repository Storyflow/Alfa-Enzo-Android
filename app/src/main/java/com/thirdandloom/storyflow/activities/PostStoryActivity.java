package com.thirdandloom.storyflow.activities;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.service.UploadStoriesService;
import com.thirdandloom.storyflow.utils.ActivityUtils;
import com.thirdandloom.storyflow.utils.DateUtils;
import com.thirdandloom.storyflow.utils.UriUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.animations.SpringAnimation;
import com.thirdandloom.storyflow.utils.image.PhotoFileUtils;
import com.thirdandloom.storyflow.views.edittext.OpenEventDetectorEditText;
import com.thirdandloom.storyflow.views.PostStoryBar;
import com.thirdandloom.storyflow.views.SizeNotifierFrameLayout;
import com.thirdandloom.storyflow.views.emoji.CatsStickersView;
import com.thirdandloom.storyflow.views.emoji.KeyboardController;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.io.Serializable;

public class PostStoryActivity extends EmojiKeyboardActivity {
    private static final int CAPTURE_PHOTO = 1;
    private static final int SELECT_PHOTO = CAPTURE_PHOTO + 1;
    private static final int ENHANCE_PHOTO = SELECT_PHOTO + 1;
    private static final String PENDING_STORY_LOCAL_UID = "PENDING_STORY_LOCAL_UID";

    public static Intent newInstance() {
        return new Intent(StoryflowApplication.applicationContext, PostStoryActivity.class);
    }

    private PostStoryBar postStoryBar;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private View keyboardReplacerView;
    private KeyboardController keyboardController;
    private ScrollView scrollViewContainer;
    private int defaultScrollViewHeight;
    private ImageView postStoryImageView;
    private View enhancePostStoryImageView;
    private View deletePostStoryImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_story);
        setTitle(R.string.post_story);
        findViews();
        initGui();
        initListeners();
        restoreState(SavedState.class, savedInstanceState,
                restored -> state = restored,
                inited -> state = inited);
        ViewUtils.callOnPreDraw(scrollViewContainer, view -> {
            defaultScrollViewHeight = view.getHeight();
        });
        if (savedInstanceState == null) keyboardController.openKeyboardInternal();
    }

    private void findViews() {
        postStoryBar = (PostStoryBar)findViewById(R.id.activity_post_story_post_bar);
        sizeNotifierLayout = (SizeNotifierFrameLayout)findViewById(R.id.activity_post_story_size_notifier);
        keyboardReplacerView = findViewById(R.id.activity_post_story_keyboard_replacer);
        editText = (OpenEventDetectorEditText)findViewById(R.id.activity_post_story_edit_text);
        emojiContainerView = findViewById(R.id.activity_post_story_emoji_container);
        catsStickersView = (CatsStickersView)findViewById(R.id.activity_post_story_cats_emoji);
        scrollViewContainer = (ScrollView)findViewById(R.id.activity_post_story_scroll_view);
        postStoryImageView = (ImageView)findViewById(R.id.activity_post_story_image_view);
        enhancePostStoryImageView = findViewById(R.id.activity_post_story_enhance);
        deletePostStoryImageView = findViewById(R.id.activity_post_story_delete);
    }

    private void initListeners() {
        enhancePostStoryImageView.setOnClickListener(v -> {
            Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                    .setData(Uri.parse(state.capturedAbsolutePhotoPath))
                    .build();
            startActivityForResult(imageEditorIntent, ENHANCE_PHOTO);
        });
        deletePostStoryImageView.setOnClickListener(v -> {
            ViewUtils.hide(enhancePostStoryImageView, deletePostStoryImageView, postStoryImageView);
            state.capturedAbsolutePhotoPath = null;
            postStoryImageView.setImageDrawable(null);
        });
        SpringAnimation.init(enhancePostStoryImageView);
        SpringAnimation.initVisibleAfterClick(deletePostStoryImageView);
    }

    private void initGui() {
        initializeEmoji();
        keyboardController = new KeyboardController(editText, keyboardReplacerView);
        keyboardController.setKeyboardStateUpdater(this::updatePostStoryBarIcons);
        keyboardController.setKeyboardWillAppear(this::onKeyboardWillAppear);
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
    public int getStatusBarColorResourceId() {
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
                newScrollViewHeight = onEmojiSelected();
                break;
            case Cats:
                newScrollViewHeight = onCatsSelected();
                break;
            case Native:
                newScrollViewHeight = onNativeSelected();
                break;
            case None:
                newScrollViewHeight = onNoneSelected();
                break;
            default:
                throw new UnsupportedOperationException("KeyboardController.Keyboard unsupported type is using");
        }
        keyboardTypeDidChange(keyboardType, newScrollViewHeight);
    }

    private void keyboardTypeDidChange(KeyboardController.Keyboard keyboardType, int newScrollViewHeight) {
        editText.setCursorVisible(keyboardType != KeyboardController.Keyboard.None);
        ViewUtils.applyHeight(scrollViewContainer, newScrollViewHeight);
        if (keyboardType != KeyboardController.Keyboard.None) {
            editText.requestFocus();
            StoryflowApplication.runOnUIThread(() -> {
                editText.keyboardDidAppear();
                editText.requestLayout();
            }, 300);
        }
    }

    private int onNoneSelected() {
        postStoryBar.onNoneSelected();
        return defaultScrollViewHeight;
    }

    private int onNativeSelected() {
        hideEmoji(keyboardController.getKeyboardHeight());
        hideCatsEmoji(keyboardController.getKeyboardHeight());
        postStoryBar.onNativeKeyboardSelected();
        return defaultScrollViewHeight - keyboardController.getKeyboardHeight();
    }

    private int onCatsSelected() {
        showCatsEmoji();
        hideEmoji(keyboardController.getKeyboardHeight());
        postStoryBar.onCastSelected();
        return defaultScrollViewHeight - keyboardController.getKeyboardHeight();
    }

    private int onEmojiSelected() {
        showEmoji();
        hideCatsEmoji(keyboardController.getKeyboardHeight());
        postStoryBar.onEmojiSelected();
        return defaultScrollViewHeight - keyboardController.getKeyboardHeight();
    }

    private void onKeyboardWillAppear(int keyboardHeight) {
        int keyboardAppearingHeight = defaultScrollViewHeight - keyboardHeight
                + postStoryBar.getHeight();
        editText.keyboardWillAppear(keyboardAppearingHeight);
        editText.requestFocus();
    }

    private final PostStoryBar.Actions postStoryActions = new PostStoryBar.Actions() {
        @Override
        public void onPostStoryClicked() {
            PendingStory story = new PendingStory();
            String description = editText.getText().toString().trim();
            description = TextUtils.isEmpty(description)
                    ? null
                    : description;

            if (description == null && TextUtils.isEmpty(state.capturedAbsolutePhotoPath)) {
                showWarning(R.string.story_should_contain_at_least_one_character);
            } else {
                story.setData(description, DateUtils.todayCalendar().getTime());
                story.setImageData(state.capturedAbsolutePhotoPath, postStoryImageView.getWidth(), postStoryImageView.getHeight());
                StoryflowApplication.getPendingStoriesManager().add(story);
                UploadStoriesService.notifyService();
                setResult(RESULT_OK, createResultIntent(story));
                finish();
            }
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
            keyboardController.openKeyboard();
        }

        @Override
        public void onCatsClicked() {
            keyboardController.catsClicked();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAPTURE_PHOTO:
                    break;
                case SELECT_PHOTO:
                    state.capturedAbsolutePhotoPath = UriUtils.getPath(this, Uri.parse(data.getData().toString()));
                    break;
                case ENHANCE_PHOTO:
                    state.capturedAbsolutePhotoPath = data.getData().toString();
                    break;
            }
            ViewUtils.show(postStoryImageView, deletePostStoryImageView, enhancePostStoryImageView);
            Glide
                    .with(this)
                    .load(state.capturedAbsolutePhotoPath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(postStoryImageView);
        }
    }

    public static Intent createResultIntent(PendingStory story) {
        Intent intent = new Intent();
        intent.putExtra(PENDING_STORY_LOCAL_UID, story);
        return intent;
    }

    public static PendingStory extractResult(Intent intent) {
        return (PendingStory) intent.getSerializableExtra(PENDING_STORY_LOCAL_UID);
    }

    @Nullable
    @Override
    protected Serializable getSavedState() {
        return state;
    }

    @Override
    protected Serializable getInitState() {
        return new SavedState();
    }

    private SavedState state;

    private static class SavedState implements Serializable {
        private static final long serialVersionUID = 8645864584763024484L;
        String capturedAbsolutePhotoPath;
    }
}
