package com.thirdandloom.storyflow.utils;

import android.support.v7.widget.LinearLayoutManager;

public class RecyclerLayoutManagerUtils extends BaseUtils {

    public static int getCurrentVisiblePosition(LinearLayoutManager linearLayoutManager) {
        int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
        int position = lastVisiblePosition - firstVisiblePosition == 0
                ? firstVisiblePosition
                : firstVisiblePosition + 1;
        return position;
    }

}
