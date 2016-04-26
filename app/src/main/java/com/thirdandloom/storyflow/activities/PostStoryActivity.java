package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.views.OpenEventDetectorEditText;
import com.thirdandloom.storyflow.views.PostStoryBar;
import com.thirdandloom.storyflow.views.SizeNotifierFrameLayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class PostStoryActivity extends BaseActivity {

    private PostStoryBar postStoryBar;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private View keyboardReplacerView;
    private OpenEventDetectorEditText postStoryEditText;
    private int keyboardHeight;
    private boolean keyboardIsVisible;
    private boolean emojiPopupIsVisible;
    private boolean keyboardReplaceViewIsVisible;
    private boolean waitingForKeyboardOpen;

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

        openKeyboardInternal();
    }

    private void findViews() {
        postStoryBar = (PostStoryBar)findViewById(R.id.activity_post_story_post_bar);
        sizeNotifierLayout = (SizeNotifierFrameLayout)findViewById(R.id.activity_post_story_size_notifier);
        keyboardReplacerView = findViewById(R.id.activity_post_story_keyboard_replacer);
        postStoryEditText = (OpenEventDetectorEditText)findViewById(R.id.activity_post_story_edit_text);
    }

    private void initGui() {
        postStoryEditText.setOpenEvent(this::openKeyboardInternal);
        postStoryBar.setActions(postStoryActions);
        sizeNotifierLayout.setActions(appearedHeight -> {
            if (appearedHeight > AndroidUtils.dp(50) && !keyboardIsVisible) {
                keyboardDidAppear(appearedHeight);
            } else if (appearedHeight < AndroidUtils.dp(50) && keyboardIsVisible) {
                keyboardDidDisappear();
            }

            if (keyboardIsVisible && waitingForKeyboardOpen) {
                waitingForKeyboardOpen = false;
                StoryflowApplication.cancelRunOnUIThread(openKeyboardRunnable);
            }
        });
    }

    private void keyboardDidDisappear() {
        keyboardIsVisible = false;

        if (emojiPopupIsVisible) {
            postStoryBar.emojiDidSelect();
        } else {
            ViewUtils.applyHeight(keyboardReplacerView, 0);
            keyboardReplaceViewIsVisible = false;
            postStoryBar.keyboardDidSelect();
        }
    }

    private void keyboardDidAppear(int appearedHeight) {
        keyboardIsVisible = true;
        keyboardHeight = appearedHeight;
        if (!keyboardReplaceViewIsVisible) {
            keyboardReplaceViewIsVisible = true;
            ViewUtils.applyHeight(keyboardReplacerView, keyboardHeight);
        }

        if (emojiPopupIsVisible) {
            emojiPopupIsVisible = false;
        }
        postStoryBar.keyboardDidSelect();
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
        if (emojiPopupIsVisible) {
            emojiPopupIsVisible = false;

            if (keyboardReplaceViewIsVisible) {
                keyboardReplaceViewIsVisible = false;
                ViewUtils.applyHeight(keyboardReplacerView, 0);
            }

            if (emojiPopupIsVisible) {
                postStoryBar.emojiDidSelect();
            } else {
                postStoryBar.keyboardDidSelect();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void closeKeyboardInternal() {
        AndroidUtils.hideKeyboard(postStoryEditText);
    }

    private void openKeyboardInternal() {
        postStoryEditText.requestFocus();
        AndroidUtils.showKeyboard(postStoryEditText);
        if (!keyboardIsVisible) {
            waitingForKeyboardOpen = true;
            StoryflowApplication.cancelRunOnUIThread(openKeyboardRunnable);
            StoryflowApplication.runOnUIThread(openKeyboardRunnable, 100);
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
            Timber.d("PostStoryBar Actions onEmojiClicked");
            if (!emojiPopupIsVisible) {
                emojiPopupIsVisible = true;
                if (keyboardIsVisible) {
                    closeKeyboardInternal();
                } else {
                    if (!keyboardReplaceViewIsVisible) {
                        keyboardReplaceViewIsVisible = true;
                        ViewUtils.applyHeight(keyboardReplacerView, keyboardHeight);
                    }
                }
            } else {
                emojiPopupIsVisible = false;
                openKeyboardInternal();
            }

            if (emojiPopupIsVisible) {
                postStoryBar.emojiDidSelect();
            } else {
                postStoryBar.keyboardDidSelect();
            }
        }
    };

    private final Runnable openKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            if (postStoryEditText != null && waitingForKeyboardOpen && !keyboardIsVisible) {
                postStoryEditText.requestFocus();
                AndroidUtils.showKeyboard(postStoryEditText);
                StoryflowApplication.cancelRunOnUIThread(openKeyboardRunnable);
                StoryflowApplication.runOnUIThread(openKeyboardRunnable, 100);
            }
        }
    };
}
