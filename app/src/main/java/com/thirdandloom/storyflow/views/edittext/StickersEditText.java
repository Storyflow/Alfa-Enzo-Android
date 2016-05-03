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
    private static final String FIND_REG_EXP = "\\[(\\w*?)\\]";
    private static final String FIND_DELETED_REG_EXP = "\\[\\s*(\\w+)*$|\\[\\s*(\\w+)*\\s|\\[\\s*(\\w+)*[\\[]";
    private static final Pattern FIND_IMAGE_PATTERN = Pattern.compile(FIND_REG_EXP);
    private static final Pattern FIND_DELETE_IMAGE_PATTERN = Pattern.compile(FIND_DELETED_REG_EXP);

    private static List<DisplayedSticker> oldDetectedStickers = new ArrayList<>();

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
    protected void onSelectionChanged(int selStart, int selEnd) {
        boolean selectionWasModified = false;

        if (selStart != selEnd) {
            String currentText = getText().toString();
            String selectedText = currentText.substring(selStart, selEnd);
            boolean lastSymbolIsStartSticker = selectedText.charAt(selectedText.length() - 1) == "[".charAt(0);
            boolean firstSymbolIsEndSticker = selectedText.charAt(0) == "]".charAt(0);

            if (lastSymbolIsStartSticker) {
                String detectedSticker = getStickerWithStartStickerPos(selEnd - 1);
                selectionWasModified = detectedSticker.length() > 1;
                selEnd = selEnd + detectedSticker.length() - 1;
            }
            if (firstSymbolIsEndSticker) {
                String detectedSticker = getStickerWithEndStickerPos(selStart + 1);
                if (detectedSticker.length() > 1) {
                    selectionWasModified = true;
                    selStart = selStart + 1;
                }
            }
        }
        if (!selectionWasModified) {
            super.onSelectionChanged(selStart, selEnd);
        } else {
            setSelection(selStart, selEnd);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        int oldTextLength = getText().length();
        int oldSelectionEnd = getSelectionEnd();
        getTextWithImages(getText(), getSelectionStart(), getSelectionEnd(), newText -> {
            setText(newText);
            setSelection(oldSelectionEnd-(oldTextLength - newText.length()));
        }, () -> super.onTextChanged(text, start, lengthBefore, lengthAfter));
    }

    private static void getTextWithImages(Editable text, int selectionStart, int selectionEnd,
                                          Action1<Editable> onChanged, Action0 onNotChanged) {
        Matcher matcher = FIND_IMAGE_PATTERN.matcher(text.toString());

        ImageSpan[] oldSpans = text.getSpans(0, text.length(), ImageSpan.class);
        List<ImageSpan> oldSpansList = new ArrayList<>(new ArrayList<>(Arrays.asList(oldSpans)));
        for (ImageSpan span : oldSpansList) {
            text.removeSpan(span);
        }

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

        boolean removedStickersDetected = false;
        if (oldDetectedStickers.size() > detectedStickers.size()) {
            Matcher deletedMatcher = FIND_DELETE_IMAGE_PATTERN.matcher(text.toString());
            while (deletedMatcher.find()) {
                String sticker = deletedMatcher.group();
                boolean isPart = isPart(sticker);
                if (isPart) {
                    removedStickersDetected = true;
                    StringBuilder stringBuilder = new StringBuilder(text);
                    if (sticker.charAt(sticker.length() - 1) == "[".charAt(0)) {
                        stringBuilder.replace(deletedMatcher.start() + 1, deletedMatcher.end(), "");
                    } else {
                        stringBuilder.replace(deletedMatcher.start(), deletedMatcher.end(), "");
                    }
                    text = new SpannableStringBuilder(stringBuilder);
                }
            }
        }

        if (!removedStickersDetected && selectionEnd == selectionStart && selectionEnd > 2) {
            int lastStickerPosition = selectionEnd + 1;
            String sticker = getStickerWithEndStickerPos(lastStickerPosition);
            removedStickersDetected |= sticker.length() > 2;
            StringBuilder stringBuilder = new StringBuilder(text);
            stringBuilder.replace(selectionEnd - (sticker.length() - 1), selectionEnd, "");
            text = new SpannableStringBuilder(stringBuilder);
        }

        oldDetectedStickers = detectedStickers;
        if (removedStickersDetected) {
            onChanged.call(text);
        } else {
            onNotChanged.call();
        }
    }


    /**
     *
     * @param endPosition
     * @return whitespace if not detected
     */
    private static String getStickerWithEndStickerPos(int endPosition) {
        for (DisplayedSticker sticker : oldDetectedStickers) {
            if (sticker.endPosition == endPosition) {
                return sticker.text;
            }
        }
        return " ";
    }

    /**
     *
     * @param startPosition
     * @return whitespace if not detected
     */
    private static String getStickerWithStartStickerPos(int startPosition) {
        for (DisplayedSticker sticker : oldDetectedStickers) {
            if (sticker.startPosition == startPosition) {
                return sticker.text;
            }
        }
        return " ";
    }

    private static boolean isPart(String sticker) {
        if (sticker.charAt(sticker.length() - 1) == "[".charAt(0)) {
            sticker = sticker.substring(0, sticker.length() - 2);
        }
        for (String currentSticker : Theme.Stickers.catMapKeysList) {
            if (currentSticker.contains(sticker)) {
                return true;
            }
        }
        return false;
    }

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
}
