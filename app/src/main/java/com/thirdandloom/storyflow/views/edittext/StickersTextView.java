package com.thirdandloom.storyflow.views.edittext;

import com.rockerhieu.emojicon.EmojiconTextView;
import com.thirdandloom.storyflow.utils.StickersUtils;

import android.content.Context;
import android.util.AttributeSet;

import java.util.regex.Matcher;

public class StickersTextView extends EmojiconTextView {

    public StickersTextView(Context context) {
        this(context, null);
    }

    public StickersTextView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public StickersTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        Matcher matcher = StickersEditText.FIND_IMAGE_PATTERN.matcher(text.toString());
        StickersUtils.removeImageSpans(getEditableText());
        StickersUtils.addNewSpans(matcher, getEditableText());
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

}
