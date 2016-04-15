package com.thirdandloom.storyflow.views.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class DisableScrollLinearLayoutManager extends LinearLayoutManager {
    private boolean disableScroll;

    public DisableScrollLinearLayoutManager(Context context) {
        super(context);
    }

    public DisableScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public DisableScrollLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDisableScroll(boolean disableScroll) {
        this.disableScroll = disableScroll;
    }

    @Override
    public boolean canScrollHorizontally() {
        return !disableScroll && super.canScrollHorizontally();
    }

    @Override
    public boolean canScrollVertically() {
        return !disableScroll && super.canScrollVertically();
    }
}
