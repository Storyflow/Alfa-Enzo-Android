package com.thirdandloom.storyflow.activities;

import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.views.edittext.OpenEventDetectorEditText;
import com.thirdandloom.storyflow.views.emoji.CatsStickersView;

import android.view.View;

public abstract class EmojiKeyboardActivity extends BaseActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    protected OpenEventDetectorEditText editText;
    protected View emojiContainerView;
    protected CatsStickersView catsStickersView;
    private EmojiconsFragment emojiconsFragment = EmojiconsFragment.newInstance(false);

    protected void initializeEmoji() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(getEmojiContainerId(), emojiconsFragment)
                .commit();
        catsStickersView.setOnStickerSelected(editText::append);
    }

    protected void hideEmoji(int keyboardHeight) {
        emojiContainerView.animate()
                .translationY(+keyboardHeight)
                .setDuration(100)
                .start();
    }

    protected void showEmoji() {
        emojiContainerView.animate()
                .translationY(0)
                .setDuration(100)
                .start();
    }

    protected void hideCatsEmoji(int keyboardHeight) {
        catsStickersView.animate()
                .translationY(+keyboardHeight)
                .setDuration(100)
                .start();
    }

    protected void showCatsEmoji() {
        catsStickersView.animate()
                .translationY(0)
                .setDuration(100)
                .start();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(editText, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(editText);
    }

    protected abstract int getEmojiContainerId();
}
