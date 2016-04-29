package com.thirdandloom.storyflow.views.edittext;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import rx.functions.Action0;

public class OpenEventDetectorEditTextDev extends StickersEditText {

    public OpenEventDetectorEditTextDev(Context context) {
        super(context);
    }

    public OpenEventDetectorEditTextDev(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OpenEventDetectorEditTextDev(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Action0 openEvent;

    public void setOpenEvent(Action0 openEvent) {
        this.openEvent = openEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.openEvent.call();
        }
        return super.onTouchEvent(event);
    }
}
