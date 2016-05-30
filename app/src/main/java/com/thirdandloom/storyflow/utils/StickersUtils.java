package com.thirdandloom.storyflow.utils;

import com.thirdandloom.storyflow.Theme;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.ImageSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class StickersUtils extends BaseUtils {
    public static class DisplayedSticker {
        public int startPosition;
        public int endPosition;
        public String text;

        public DisplayedSticker(int startPos, int endPos, String text) {
            this.startPosition = startPos;
            this.endPosition = endPos;
            this.text = text;
        }
    }

    public static List<DisplayedSticker> addNewSpans(Matcher matcher, Editable text) {
        if (text == null) return new ArrayList<>();
        List<DisplayedSticker> detectedStickers = new ArrayList<>();
        while (matcher.find()) {
            String sticker = matcher.group();
            if (Theme.Stickers.catMap.containsKey(sticker)) {
                int start = matcher.start();
                int end = matcher.end();
                detectedStickers.add(new DisplayedSticker(start, end, sticker));
                Drawable image = Theme.Stickers.catMap.get(sticker);
                ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BASELINE);
                text.setSpan(imageSpan,
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        return detectedStickers;
    }

    public static void removeImageSpans(Editable text) {
        if (text == null) return;
        ImageSpan[] oldSpans = text.getSpans(0, text.length(), ImageSpan.class);
        List<ImageSpan> oldSpansList = new ArrayList<>(new ArrayList<>(Arrays.asList(oldSpans)));
        for (ImageSpan span : oldSpansList) {
            text.removeSpan(span);
        }
    }
}
