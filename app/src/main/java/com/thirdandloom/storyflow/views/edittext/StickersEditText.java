package com.thirdandloom.storyflow.views.edittext;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.thirdandloom.storyflow.Theme;
import com.thirdandloom.storyflow.utils.Timber;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StickersEditText extends EmojiconEditText {
    private static final Pattern FIND_IMAGE_REG_EXP = Pattern.compile("\\[(.*?)\\]");

    public StickersEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickersEditText(Context context) {
        super(context);
    }

    public StickersEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private boolean emojiChanged;

    public void setEmojiChanged(boolean emojiChanged) {
        this.emojiChanged = emojiChanged;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        currentTimeMillist = System.currentTimeMillis();
        if (emojiChanged) {
            super.onTextChanged(text, start, lengthBefore, lengthAfter);
            emojiChanged = false;
        }
        long deltaTime = System.currentTimeMillis() - currentTimeMillist;
        Timber.d("add emojis takes millis :%d", deltaTime);

        getTextWithImages(getText());
    }


    static long currentTimeMillist;
    private static Spannable getTextWithImages(Editable text) {
        currentTimeMillist = System.currentTimeMillis();

        ImageSpan[] oldSpans = text.getSpans(0, text.length(), ImageSpan.class);
        for (int i = 0; i < oldSpans.length; i++) {
            text.removeSpan(oldSpans[i]);
        }

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
        Timber.d("getTextWithImages takes millis :%d, for text length: %d", deltaTime, text.length());
        return text;
    }
}
