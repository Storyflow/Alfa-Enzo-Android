package com.thirdandloom.storyflow.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;

public class SpannableUtils extends BaseUtils {
    public static void setOnClick(SpannableString spannableString, ClickableSpan clickableSpan, String inString, String fromString) {
        int start = fromString.indexOf(inString);
        int end = start + inString.length();
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
