package com.thirdandloom.storyflow.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundSquareImageView extends ImageView {
    public RoundSquareImageView(Context context) {
        super(context);
    }

    public RoundSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundSquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int minSize = Math.min(measuredHeight, measuredWidth);
        setMeasuredDimension(minSize, minSize);
    }
}
