package com.thirdandloom.storyflow.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import rx.functions.Action0;

public class OpenEventDetectorEditText extends EditText {

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
