package com.thirdandloom.storyflow.views.edittext;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import rx.functions.Action0;

public class OpenEventDetectorEditText extends StickersEditText {

    public OpenEventDetectorEditText(Context context) {
        super(context);
    }

    public OpenEventDetectorEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OpenEventDetectorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Action0 openEvent;
    private boolean keyboardAppearing;
    private int featureHeight;

    public void setOpenEvent(Action0 openEvent) {
        this.openEvent = openEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            openEvent.call();
        }
        return super.onTouchEvent(event);
    }

    public void keyboardWillAppear(int futureHeight) {
        this.keyboardAppearing = true;
        this.featureHeight = futureHeight;
    }

    public void keyboardDidAppear() {
        keyboardAppearing = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!keyboardAppearing) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), featureHeight);
        }
    }
}
