package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.views.OnTouchEditText;
import com.thirdandloom.storyflow.views.PostStoryBar;
import com.thirdandloom.storyflow.views.SizeNotifierFrameLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import retrofit2.http.POST;

public class PostStoryActivity extends BaseActivity {

    private PostStoryBar postStoryBar;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private View keyboardReplacerView;
    private OnTouchEditText postStoryEditText;

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
    }


    private boolean waitingForKeyboardOpen;
    private Runnable openKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            if (postStoryEditText != null && waitingForKeyboardOpen && !isVisible) {
                postStoryEditText.requestFocus();

                InputMethodManager inputManager = (InputMethodManager)postStoryEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(postStoryEditText, InputMethodManager.SHOW_IMPLICIT);

                StoryflowApplication.cancelRunOnUIThread(openKeyboardRunnable);
                StoryflowApplication.runOnUIThread(openKeyboardRunnable, 100);
            }
        }
    };
    private void openKeyboardInternal() {
        postStoryEditText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)postStoryEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(postStoryEditText, InputMethodManager.SHOW_IMPLICIT);
        if (!isVisible) {
            waitingForKeyboardOpen = true;
            StoryflowApplication.cancelRunOnUIThread(openKeyboardRunnable);
            StoryflowApplication.runOnUIThread(openKeyboardRunnable, 100);

            ViewGroup.LayoutParams params = keyboardReplacerView.getLayoutParams();
            params.height = PostStoryActivity.keyboardHeight;
            if (params.height == PostStoryActivity.keyboardHeight) {
                params.height = 10;
            }

            keyboardReplacerView.setLayoutParams(params);
        }
    }

    private void findViews() {
        postStoryBar = (PostStoryBar)findViewById(R.id.activity_post_story_post_bar);
        sizeNotifierLayout = (SizeNotifierFrameLayout)findViewById(R.id.activity_post_story_size_notifier);
        keyboardReplacerView = findViewById(R.id.activity_post_story_keyboard_replacer);
        postStoryEditText = (OnTouchEditText)findViewById(R.id.activity_post_story_edit_text);
    }

    private static int keyboardHeight;
    private static boolean isVisible;

    private void initGui() {
        postStoryEditText.setOpenIvent(() -> openKeyboardInternal()
        );
        postStoryBar.setActions(postStoryActions);
        sizeNotifierLayout.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() {
            @Override
            public void onSizeChanged(int keyboardHeight, boolean isWidthGreater) {
                Timber.d("onSizeChanged keyboard height: %d", keyboardHeight);
                if (keyboardHeight > DeviceUtils.dp(100) && !isVisible) {
                    Timber.d("keyboard visible");
                    isVisible = true;
                    PostStoryActivity.keyboardHeight = keyboardHeight - 96;

                    ViewGroup.LayoutParams params = keyboardReplacerView.getLayoutParams();
                    params.height = PostStoryActivity.keyboardHeight;
                    if (params.height == PostStoryActivity.keyboardHeight) {
                        params.height = 10;
                    }

                    keyboardReplacerView.setLayoutParams(params);

                } else if (keyboardHeight < DeviceUtils.dp(100) && isVisible) {
                    isVisible = false;
                    Timber.d("keyboard hidden previous keyboard height: %d", PostStoryActivity.keyboardHeight);
                    ViewGroup.LayoutParams params = keyboardReplacerView.getLayoutParams();
                    params.height = PostStoryActivity.keyboardHeight;
                    keyboardReplacerView.setLayoutParams(params);
                }

                if (isVisible && waitingForKeyboardOpen) {
                    waitingForKeyboardOpen = false;
                    StoryflowApplication.cancelRunOnUIThread(openKeyboardRunnable);
                }
            }
        });
    }

    @Override
    protected int getStatusBarColorResourceId() {
        return R.color.greyXLighter;
    }

    @Override
    protected boolean hasToolBar() {
        return true;
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
    };
}
