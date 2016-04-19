package com.thirdandloom.storyflow.utils;

import android.support.v7.widget.LinearLayoutManager;

public class RecyclerLayoutManagerUtils extends BaseUtils {
    public static int getCurrentVisiblePosition(LinearLayoutManager linearLayoutManager) {
        int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
        return MathUtils.getIncrementedFirstPosition(firstVisiblePosition, lastVisiblePosition);
    }
}
