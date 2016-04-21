package com.thirdandloom.storyflow.views;

import com.thirdandloom.storyflow.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

public class RoundSquareStrokeCircleImageView extends RoundSquareImageView {

    public RoundSquareStrokeCircleImageView(Context context) {
        this(context, null);
    }

    public RoundSquareStrokeCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundSquareStrokeCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint pathPaint;
    private Paint circlePaint;
    private Path path = new Path();
    private int radius;

    private void init() {
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(getResources().getColor(R.color.white));

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(getResources().getColor(R.color.greyWhite));
        circlePaint.setStrokeWidth(getStrokeWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = getWidth()/2;

        path.reset();
        path.moveTo(0, 0);
        path.lineTo(0, h);
        path.lineTo(w, h);
        path.lineTo(w, 0);
        path.addCircle(radius, radius, radius - getStrokeWidth()/2, Path.Direction.CW);
        path.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, pathPaint);
        canvas.drawCircle(radius, radius, radius - getStrokeWidth()/2, circlePaint);
    }

    private int getStrokeWidth() {
        return getResources().getDimensionPixelSize(R.dimen.sizeMedium);
    }
}
