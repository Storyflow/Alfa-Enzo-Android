package com.thirdandloom.storyflow.views.edittext;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.thirdandloom.storyflow.Theme;

import rx.functions.Action0;
import rx.functions.Action1;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
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
    private static final String FIND_REG_EXP = "\\[(\\w*?)\\]|\\[(\\w*?)*-(\\w*?)\\]";
    private static final String FIND_DELETED_REG_EXP = "\\[\\s*(\\w+)*$|\\[\\s*(\\w+)*\\s|\\[\\s*(\\w+)*[\\[]";
    private static final Pattern FIND_IMAGE_PATTERN = Pattern.compile(FIND_REG_EXP);
    private static final Pattern FIND_DELETE_IMAGE_PATTERN = Pattern.compile(FIND_DELETED_REG_EXP);
    private static final char START_STICKER = "[".charAt(0);
    private static final char END_STICKER = "]".charAt(0);

    private List<DisplayedSticker> oldDetectedStickers;

    public StickersEditText(Context context) {
        this(context, null);
    }

    public StickersEditText(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public StickersEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public List<DisplayedSticker> getOldDetectedStickers() {
        if (oldDetectedStickers == null) {
            oldDetectedStickers = new ArrayList<>();
        }
        return oldDetectedStickers;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        boolean selectionWasModified = false;

        if (selStart != selEnd) {
            String currentText = getText().toString();
            String selectedText = currentText.substring(selStart, selEnd);
            boolean lastSymbolIsStartSticker = selectedText.charAt(selectedText.length() - 1) == START_STICKER;
            boolean firstSymbolIsEndSticker = selectedText.charAt(0) == END_STICKER;

            if (lastSymbolIsStartSticker) {
                String detectedSticker = getStickerWithStartStickerPos(selEnd - 1, getOldDetectedStickers());
                selectionWasModified = detectedSticker.length() > 1;
                selEnd = selEnd + detectedSticker.length() - 1;
            }
            if (firstSymbolIsEndSticker) {
                String detectedSticker = getStickerWithEndStickerPos(selStart + 1, getOldDetectedStickers());
                if (detectedSticker.length() > 1) {
                    selectionWasModified = true;
                    selStart = selStart + 1;
                }
            }
        }
        if (!selectionWasModified && selStart == selEnd) {
            DisplayedSticker sticker = getDisplayedStickerForPosition(selStart, getOldDetectedStickers());
            if (sticker != null) {
                selectionWasModified = true;
                selStart = selStart == sticker.startPosition
                        ? selStart
                        : sticker.endPosition;
                selEnd = selStart;
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
        getTextWithImages(getOldDetectedStickers(), getText(), getSelectionStart(), getSelectionEnd()
                , newText -> {
            setText(newText);
            setSelection(oldSelectionEnd - (oldTextLength - newText.length()));
        }, () -> super.onTextChanged(text, start, lengthBefore, lengthAfter)
                , (detectedStickers) -> oldDetectedStickers = detectedStickers);
    }

    private static void getTextWithImages(List<DisplayedSticker> oldDetectedStickers, Editable text, int selectionStart, int selectionEnd,
                                          Action1<Editable> onChanged, Action0 onNotChanged, Action1<List<DisplayedSticker>> oldStickersUpdate) {
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
                    if (sticker.charAt(sticker.length() - 1) == START_STICKER) {
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
            String sticker = getStickerWithEndStickerPos(lastStickerPosition, oldDetectedStickers);
            removedStickersDetected = sticker.length() > 2;
            StringBuilder stringBuilder = new StringBuilder(text);
            stringBuilder.replace(selectionEnd - (sticker.length() - 1), selectionEnd, "");
            text = new SpannableStringBuilder(stringBuilder);
        }

        oldStickersUpdate.call(detectedStickers);
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
    private static String getStickerWithEndStickerPos(int endPosition, List<DisplayedSticker> oldDetectedStickers) {
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
    private static String getStickerWithStartStickerPos(int startPosition, List<DisplayedSticker> oldDetectedStickers) {
        for (DisplayedSticker sticker : oldDetectedStickers) {
            if (sticker.startPosition == startPosition) {
                return sticker.text;
            }
        }
        return " ";
    }

    @Nullable
    private static DisplayedSticker getDisplayedStickerForPosition(int position, List<DisplayedSticker> detectedStickers) {
        for (DisplayedSticker sticker :  detectedStickers) {
            if (sticker.startPosition <= position
                    && sticker.endPosition >= position) {
                return sticker;
            }
        }
        return null;
    }

    private static boolean isPart(String sticker) {
        if (sticker.charAt(sticker.length() - 1) == START_STICKER) {
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
