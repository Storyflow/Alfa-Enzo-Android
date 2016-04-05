package com.thirdandloom.storyflow.utils.image;

import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.thirdandloom.storyflow.utils.BaseUtils;

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

    public static Rect getRect(@NonNull String rectString) {
        String[] splitedString = rectString.split(DELIMITER);
        int x = Integer.valueOf(splitedString[0]);
        int y = Integer.valueOf(splitedString[1]);
        int width = Integer.valueOf(splitedString[2]);
        int height = Integer.valueOf(splitedString[3]);
        return new Rect(x, y, x+width, y+height);
    }

    private static String getRectString(Rect roundRect) {
        int x = roundRect.left;
        int y = roundRect.top;
        int width = roundRect.width();
        int height = roundRect.height();
        return x + DELIMITER + y + DELIMITER + width + DELIMITER + height;
    }

}
