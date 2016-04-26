package com.thirdandloom.storyflow.views.emoji;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.views.OpenEventDetectorEditText;
import com.thirdandloom.storyflow.views.SizeNotifierFrameLayout;
import rx.functions.Action0;
import rx.functions.Action1;

import android.view.View;

public class KeyboardController implements SizeNotifierFrameLayout.Actions {

    private final View keyboardReplacerView;
    private final OpenEventDetectorEditText openEventDetectorEditText;

    private int keyboardHeight;
    private boolean keyboardIsVisible;
    private boolean emojiPopupIsVisible;
    private boolean keyboardReplaceViewIsVisible;
    private boolean waitingForKeyboardOpen;
    private Action1<Boolean> emojiPopupVisibilityUpdater;

    public KeyboardController(OpenEventDetectorEditText editText, View keyboardReplacerView) {
        this.openEventDetectorEditText = editText;
        this.keyboardReplacerView = keyboardReplacerView;
        openEventDetectorEditText.setOpenEvent(this::openKeyboardInternal);
    }

    public int getKeyboardHeight() {
        return keyboardHeight;
    }

    public void setEmojiPopupVisibilityUpdater(Action1<Boolean> emojiPopupVisibilityUpdater) {
        this.emojiPopupVisibilityUpdater = emojiPopupVisibilityUpdater;
    }

    public void onEmojiClicked() {
        if (!emojiPopupIsVisible) {
            if (keyboardIsVisible) {
                closeKeyboardInternal();
            } else {
                if (!keyboardReplaceViewIsVisible) {
                    showKeyboardReplacerView();
                }
            }
        } else {
            openKeyboardInternal();
        }
        emojiPopupIsVisible = !emojiPopupIsVisible;
        emojiPopupVisibilityUpdated();
    }

    public void handleBackPressed(Action0 notHandled) {
        if (emojiPopupIsVisible) {
            emojiPopupIsVisible = false;
            if (keyboardReplaceViewIsVisible) {
                hideKeyboardReplacerView();
            }
            emojiPopupVisibilityUpdated();
        } else {
            notHandled.call();
        }
    }

    @Override
    public void onSizeChanged(int appearedHeight) {
        if (appearedHeight > AndroidUtils.dp(50) && !keyboardIsVisible) {
            keyboardDidAppear(appearedHeight);
        } else if (appearedHeight < AndroidUtils.dp(50) && keyboardIsVisible) {
            keyboardDidDisappear();
        }

        if (keyboardIsVisible && waitingForKeyboardOpen) {
            waitingForKeyboardOpen = false;
            StoryflowApplication.cancelRunOnUIThread(openKeyboardRunnable);
        }
    }

    private void keyboardDidDisappear() {
        keyboardIsVisible = false;
        emojiPopupVisibilityUpdated();
        if (!emojiPopupIsVisible) {
            hideKeyboardReplacerView();
        }
    }

    private void keyboardDidAppear(int appearedHeight) {
        keyboardIsVisible = true;
        keyboardHeight = appearedHeight;
        if (!keyboardReplaceViewIsVisible) {
            showKeyboardReplacerView();
        }

        if (emojiPopupIsVisible) {
            emojiPopupIsVisible = false;
        }
        emojiPopupVisibilityUpdated();
    }

    private void hideKeyboardReplacerView() {
        keyboardReplaceViewIsVisible = false;
        ViewUtils.applyHeight(keyboardReplacerView, 0);
    }

    private void showKeyboardReplacerView() {
        keyboardReplaceViewIsVisible = true;
        ViewUtils.applyHeight(keyboardReplacerView, keyboardHeight);
    }

    private void closeKeyboardInternal() {
        AndroidUtils.hideKeyboard(openEventDetectorEditText);
    }

    public void openKeyboardInternal() {
        openEventDetectorEditText.requestFocus();
        AndroidUtils.showKeyboard(openEventDetectorEditText);
        if (!keyboardIsVisible) {
            waitingForKeyboardOpen = true;
            StoryflowApplication.cancelRunOnUIThread(openKeyboardRunnable);
            StoryflowApplication.runOnUIThread(openKeyboardRunnable, 100);
        }
    }


    private void emojiPopupVisibilityUpdated() {
        emojiPopupVisibilityUpdater.call(emojiPopupIsVisible);
    }

    private final Runnable openKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            if (openEventDetectorEditText != null && waitingForKeyboardOpen && !keyboardIsVisible) {
                openEventDetectorEditText.requestFocus();
                AndroidUtils.showKeyboard(openEventDetectorEditText);
                StoryflowApplication.cancelRunOnUIThread(openKeyboardRunnable);
                StoryflowApplication.runOnUIThread(openKeyboardRunnable, 100);
            }
        }
    };
}
