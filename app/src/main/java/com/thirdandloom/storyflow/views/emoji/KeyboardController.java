package com.thirdandloom.storyflow.views.emoji;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.views.edittext.OpenEventDetectorEditText;
import com.thirdandloom.storyflow.views.SizeNotifierFrameLayout;
import rx.functions.Action0;
import rx.functions.Action1;

import android.view.View;

public class KeyboardController implements SizeNotifierFrameLayout.Actions {

    public enum Keyboard {
        Native, Cats, Emoji, None
    }

    private final View keyboardReplacerView;
    private final OpenEventDetectorEditText openEventDetectorEditText;

    private int keyboardHeight;
    private Keyboard currentKeyboard = Keyboard.None;

    private boolean keyboardIsVisible;
    private boolean keyboardReplaceViewIsVisible;
    private boolean waitingForKeyboardOpen;
    private Action1<Keyboard> keyboardStateUpdater;
    private Action1<Integer> keyboardWillAppear;

    public KeyboardController(OpenEventDetectorEditText editText, View keyboardReplacerView) {
        this.openEventDetectorEditText = editText;
        this.keyboardReplacerView = keyboardReplacerView;
        this.openEventDetectorEditText.setOpenEvent(this::openKeyboardFromEditText);
    }

    public void setKeyboardWillAppear(Action1<Integer> keyboardWillAppear) {
        this.keyboardWillAppear = keyboardWillAppear;
    }

    public int getKeyboardHeight() {
        return keyboardHeight;
    }

    public void setKeyboardStateUpdater(Action1<Keyboard> keyboardStateUpdater) {
        this.keyboardStateUpdater = keyboardStateUpdater;
    }

    public void onEmojiClicked() {
        if (currentKeyboard == Keyboard.Emoji) return;
        checkKeyboardReplacerView();
        currentKeyboard = Keyboard.Emoji;
        updateKeyboardVisibility();
    }

    public void catsClicked() {
        if (currentKeyboard == Keyboard.Cats) return;
        checkKeyboardReplacerView();
        currentKeyboard = Keyboard.Cats;
        updateKeyboardVisibility();
    }

    public void handleBackPressed(Action0 notHandled) {
        if (currentKeyboard != Keyboard.None) {
            currentKeyboard = Keyboard.None;
            if (keyboardReplaceViewIsVisible) {
                hideKeyboardReplacerView();
            }
            updateKeyboardVisibility();
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
        if (currentKeyboard == Keyboard.Native) {
            hideKeyboardReplacerView();
            currentKeyboard = Keyboard.None;
        }
        updateKeyboardVisibility();
        openEventDetectorEditText.setFocusableInTouchMode(currentKeyboard == Keyboard.None);
    }

    private void keyboardDidAppear(int appearedHeight) {
        keyboardIsVisible = true;
        currentKeyboard = Keyboard.Native;
        keyboardHeight = appearedHeight;
        showKeyboardReplacerView();
        updateKeyboardVisibility();
        openEventDetectorEditText.setFocusableInTouchMode(false);
    }

    private void checkKeyboardReplacerView() {
        if (currentKeyboard == Keyboard.None) {
            showKeyboardReplacerView();
        } else if (currentKeyboard == Keyboard.Native) {
            closeKeyboardInternal();
        }
    }

    private void hideKeyboardReplacerView() {
        keyboardReplaceViewIsVisible = false;
        ViewUtils.applyHeight(keyboardReplacerView, 0);
    }

    private void showKeyboardReplacerView() {
        if (!keyboardReplaceViewIsVisible) {
            keyboardReplaceViewIsVisible = true;
            ViewUtils.applyHeight(keyboardReplacerView, keyboardHeight);
        }
    }

    private void closeKeyboardInternal() {
        AndroidUtils.hideKeyboard(openEventDetectorEditText);
    }

    public void openKeyboardFromEditText() {
        if (currentKeyboard == Keyboard.None) openKeyboard();
    }

    public void openKeyboard() {
        if (currentKeyboard == Keyboard.Native) return;
        if (keyboardWillAppear != null && currentKeyboard == Keyboard.None) keyboardWillAppear.call(keyboardHeight);

        currentKeyboard = Keyboard.Native;
        AndroidUtils.showKeyboard(openEventDetectorEditText);
        updateKeyboardVisibility();
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

    private void updateKeyboardVisibility() {
        keyboardStateUpdater.call(currentKeyboard);
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
