package com.thirdandloom.storyflow.views.edittext;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.thirdandloom.storyflow.Theme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StickersEditText extends EmojiconEditText {
    private static final String START_SYMBOL = "[";
    private static final String END_SYMBOL = "]";
    private static final Pattern FIND_IMAGE_REG_EXP = Pattern.compile("\\[(.*?)\\]");

    public StickersEditText(Context context) {
        this(context, null);
    }

    public StickersEditText(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public StickersEditText(Context context, AttributeSet attrs) {
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

        private Detecting detectingStatus;
        private String detectedIcon;

        public EditTextInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            if (text.length() == 1) {
                if (text.charAt(0) == START_SYMBOL.charAt(0)) {
                    detectingStatus = Detecting.Start;
                    return super.setComposingText(text, newCursorPosition);

                } else if (text.charAt(0) == END_SYMBOL.charAt(0) && detectingStatus == Detecting.Start) {
                    detectingStatus = Detecting.End;
                    text = START_SYMBOL + detectedIcon + END_SYMBOL;
                } else {
                    detectingStatus = Detecting.None;
                }
            }

            String enteredText = text.toString();
            if (detectingStatus == Detecting.Start
                    && Theme.Stickers.catMapKeysList.contains(START_SYMBOL + enteredText + END_SYMBOL)) {
                return false;
            } else {
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
            return super.setComposingText(text, newCursorPosition);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    private void init() {
        setFilters(new InputFilter[] { filter });
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    static long currentTimeMillist;
    private static Spannable getTextWithImages(Editable text) {
        currentTimeMillist = System.currentTimeMillis();

        //ImageSpan[] oldSpans = text.getSpans(0, text.length(), ImageSpan.class);
        //for (int i = 0; i < oldSpans.length; i++) {
        //    text.removeSpan(oldSpans[i]);
        //}

        Matcher matcher = FIND_IMAGE_REG_EXP.matcher(text.toString());
        while (matcher.find()) {
            String sticker = matcher.group();
            if (Theme.Stickers.catMap.containsKey(sticker)) {
                Drawable image = Theme.Stickers.catMap.get(sticker);
                text.setSpan(new ImageSpan(image, ImageSpan.ALIGN_BASELINE),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }

        long deltaTime = System.currentTimeMillis() - currentTimeMillist;
        return text;
    }


    private String detectedSticker;
    private final InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, final Spanned dest, final int dstart, final int dend) {
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
