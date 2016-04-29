package com.thirdandloom.storyflow.views.edittext;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.thirdandloom.storyflow.Theme;
import rx.functions.Action0;
import rx.functions.Action1;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StickersEditText extends EmojiconEditText {
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

    private void init() {

    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        getTextWithImages(getText(), newText -> {
            setText(newText);
            setSelection(newText.length());
        }, () -> super.onTextChanged(text, start, lengthBefore, lengthAfter));
    }


    private static List<String> oldDetectedStickers = new ArrayList<>();
    private static void getTextWithImages(Editable text, Action1<Editable> onChanged, Action0 onNotChanged) {
        Matcher matcher = FIND_IMAGE_REG_EXP.matcher(text.toString());

        ImageSpan[] oldSpans = text.getSpans(0, text.length(), ImageSpan.class);
        List<ImageSpan> oldSpansList = new ArrayList<>(new ArrayList<>(Arrays.asList(oldSpans)));
        for (ImageSpan span : oldSpansList) {
            text.removeSpan(span);
        }

        List<String> detectedStickers = new ArrayList<>();
        while (matcher.find()) {
            String sticker = matcher.group();
            if (Theme.Stickers.catMap.containsKey(sticker)) {
                detectedStickers.add(sticker);
                Drawable image = Theme.Stickers.catMap.get(sticker);
                ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BASELINE);
                text.setSpan(imageSpan,
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }

        oldDetectedStickers.removeAll(detectedStickers);
        boolean removedStickersDetected = oldDetectedStickers.size() != 0;
        if (removedStickersDetected) {
            for (String removedSticker : oldDetectedStickers) {
                String removed = removedSticker.replace("]", "");
                text = new SpannableStringBuilder(text.toString().replace(removed, ""));
            }
        }
        oldDetectedStickers = detectedStickers;
        if (removedStickersDetected) {
            onChanged.call(text);
        } else {
            onNotChanged.call();
        }
    }
}
