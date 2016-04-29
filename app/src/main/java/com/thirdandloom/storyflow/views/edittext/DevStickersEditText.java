package com.thirdandloom.storyflow.views.edittext;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.thirdandloom.storyflow.Theme;
import com.thirdandloom.storyflow.utils.Timber;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

public class DevStickersEditText extends EmojiconEditText {
    private static final String START_SYMBOL = "[";
    private static final String END_SYMBOL = "]";

    public DevStickersEditText(Context context) {
        this(context, null);
    }

    public DevStickersEditText(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public DevStickersEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection con = super.onCreateInputConnection(outAttrs);
        EditTextInputConnection connectionWrapper = new EditTextInputConnection(con, true);
        return connectionWrapper;
    }

    public enum Detecting {
        Start, End,None
    }

    private class EditTextInputConnection extends InputConnectionWrapper {

        private Detecting detectingStatus = Detecting.None;
        private String detectedIcon;
        private String failedText;

        public EditTextInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            if (text.length() == 1) {
                if (text.charAt(0) == START_SYMBOL.charAt(0) && detectingStatus == Detecting.None) {
                    detectingStatus = Detecting.Start;
                    return super.setComposingText(text, newCursorPosition);

                } else if (text.charAt(0) == END_SYMBOL.charAt(0) && detectingStatus == Detecting.Start) {
                    detectingStatus = Detecting.End;
                    text = START_SYMBOL + detectedIcon + END_SYMBOL;
                } else if (detectingStatus == Detecting.Start && !Character.isLetter(text.charAt(0))) {
                    //user enter [ and then enter space or other symbol to commit
                    if (START_SYMBOL.charAt(0) == text.charAt(0)) {
                        detectingStatus = Detecting.Start;

                        super.commitText(START_SYMBOL + failedText, newCursorPosition);
                        return super.setComposingText(text, newCursorPosition);
                    } else {
                        detectingStatus = Detecting.None;

                        text = START_SYMBOL + failedText + text;
                        return super.commitText(text, newCursorPosition);
                    }

                    //text = START_SYMBOL + failedText + text;
                    //return super.commitText(text, newCursorPosition);
                } else {
                    detectingStatus = Detecting.None;
                }
            }

            String enteredText = text.toString();
            if (detectingStatus == Detecting.Start && text.length() != 0) { // text.length() != 0 for detecting delete action
                failedText = enteredText;
                Timber.d("test commitText return false for enteredtext:%s, and currenttext: %s, status:%s", enteredText,  text, detectingStatus.toString());
                return false;
            } else {
                Timber.d("test commitText return super.commitText for entered text:%s, and currenttext: %s, status:%s", enteredText, text,   detectingStatus.toString());
                return super.commitText(text, newCursorPosition);
            }
        }

        @Override
        public boolean setComposingText(CharSequence text, int newCursorPosition) {
            if (detectingStatus == Detecting.End) {
                detectingStatus = Detecting.None;
            } if (detectingStatus == Detecting.Start) {
                detectedIcon = text.toString();
                text = START_SYMBOL + detectedIcon;
            }
            Timber.d("test setComposingText text:%s", text);
            return super.setComposingText(text, newCursorPosition);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            int cursorPosition = getSelectionStart();
            Timber.d("test deleteSurroundingText return super.deleteSurroundingText for beforeLength:%d, and afterLength:%d, cursorPosition:%d", beforeLength, afterLength, cursorPosition);
            detectingStatus = Detecting.None;
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    private void init() {
        setFilters(new InputFilter[]{filter});
    }

    private String detectedSticker;
    private final InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, final Spanned dest, final int dstart, final int dend) {
            Timber.d("test filter source:%s start:%d  end:%d", source, start, end);
            Timber.d("test filter final destination:%s start:%d  end:%d", dest, dstart, dend);

            if ((dend - dstart) > (end - start)) {
                //user delete symbol?
                Timber.d("test filter delete symbol");
                return null;
            }

            SpannableString spannableResult = null;
            for (int i = start; i < end; i++) {
                char character = source.charAt(i);

                if (i == 0 && detectedSticker != null && character != END_SYMBOL.charAt(0)) {
                    detectedSticker = "";
                }

                if (character == END_SYMBOL.charAt(0)) {
                    if (!TextUtils.isEmpty(detectedSticker)) {
                        String mutableSticker = new String(detectedSticker);
                        mutableSticker = START_SYMBOL + mutableSticker + END_SYMBOL;

                        SpannableString sp = new SpannableString(mutableSticker);

                        if (Theme.Stickers.catMap.containsKey(mutableSticker)) {
                            if (spannableResult == null) {
                                spannableResult = new SpannableString(source);
                            }
                            Drawable image = Theme.Stickers.catMap.get(mutableSticker);
                            spannableResult.setSpan(new ImageSpan(image, ImageSpan.ALIGN_BASELINE),
                                    i - (sp.length() - 1),
                                    i - (sp.length() - 1) + sp.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                        } else {
                            if (spannableResult == null) {
                                spannableResult = new SpannableString(source);
                            }
                            spannableResult.setSpan(new SpannableString(source),
                                    i - (sp.length() - 1),
                                    i - (sp.length() - 1) + sp.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                } else if (character == START_SYMBOL.charAt(0)) {
                    detectedSticker = "";
                } else if (Character.isLetter(character) && detectedSticker != null) {
                    detectedSticker += character;
                } else {
                    detectedSticker = null;
                }
            }

            return spannableResult; //if null == keep original
        }
    };
}
