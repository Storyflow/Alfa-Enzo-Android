package com.thirdandloom.storyflow.views.emoji;

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
    private Action1<Keyboard> keyboardStateUpdater;

    public KeyboardController(OpenEventDetectorEditText editText, View keyboardReplacerView) {
        this.openEventDetectorEditText = editText;
        this.keyboardReplacerView = keyboardReplacerView;
        this.openEventDetectorEditText.setOpenEvent(this::openKeyboardInternal);
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

    public void keyboardClicked() {
        if (currentKeyboard == Keyboard.Native) return;
        openKeyboardInternal();
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
    }

    private void keyboardDidDisappear() {
        keyboardIsVisible = false;
        if (currentKeyboard == Keyboard.Native) {
            hideKeyboardReplacerView();
            currentKeyboard = Keyboard.None;
        }
        updateKeyboardVisibility();
    }

    private void keyboardDidAppear(int appearedHeight) {
        keyboardIsVisible = true;
        currentKeyboard = Keyboard.Native;
        keyboardHeight = appearedHeight;
        showKeyboardReplacerView();
        updateKeyboardVisibility();
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

    public void openKeyboardInternal() {
        currentKeyboard = Keyboard.Native;
        AndroidUtils.showKeyboard(openEventDetectorEditText);
        updateKeyboardVisibility();
    }

    private void updateKeyboardVisibility() {
        keyboardStateUpdater.call(currentKeyboard);
    }
}
