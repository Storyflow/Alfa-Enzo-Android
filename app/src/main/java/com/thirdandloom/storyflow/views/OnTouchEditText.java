package com.thirdandloom.storyflow.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by artemtkachenko on 4/25/16.
 */
public class OnTouchEditText extends EditText {

    public OnTouchEditText(Context context) {
        super(context);
    }

    public OnTouchEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public OnTouchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OnTouchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Action0 openIvent;

    public void setOpenIvent(Action0 openIvent) {
        this.openIvent = openIvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.openIvent.call();
        }
        return super.onTouchEvent(event);
    }
}
