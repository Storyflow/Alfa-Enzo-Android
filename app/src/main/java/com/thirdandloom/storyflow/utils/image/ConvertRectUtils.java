package com.thirdandloom.storyflow.utils.image;

import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.thirdandloom.storyflow.utils.BaseUtils;
import com.thirdandloom.storyflow.utils.Timber;

/**
 * #define delimiter = 'x'
 * Api format: { "rect" : "x0" + 'x' + "y0" + 'x' + "width" + 'x' + "height"}
 **/
public class ConvertRectUtils extends BaseUtils {
    private static final String DELIMITER = "x";

    public static String getRectString(RectF rectF) {
        Rect roundRect = new Rect();
        rectF.round(roundRect);
        return getRectString(roundRect);
    }

    @Nullable
    public static Rect getRect(@Nullable String rectString) {
        if (TextUtils.isEmpty(rectString)) {
            return null;
        }

        String[] splitString = rectString.split(DELIMITER);
        try {
            int x = Math.abs(Integer.valueOf(splitString[0]));
            int y = Math.abs(Integer.valueOf(splitString[1]));
            int width = Math.abs(Integer.valueOf(splitString[2]));
            int height = Math.abs(Integer.valueOf(splitString[3]));
            return new Rect(x, y, x+width, y+height);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            Timber.e(e, e.getMessage());
        }

        return null;
    }

    private static String getRectString(Rect roundRect) {
        int x = roundRect.left;
        int y = roundRect.top;
        int width = roundRect.width();
        int height = roundRect.height();
        return x + DELIMITER + y + DELIMITER + width + DELIMITER + height;
    }

}
