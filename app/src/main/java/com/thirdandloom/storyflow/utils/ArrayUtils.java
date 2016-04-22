package com.thirdandloom.storyflow.utils;

import java.util.List;

public class ArrayUtils extends BaseUtils {
    public static final int INDEX_NOT_FOUND = -1;
    public static final int EMPTY_POSITION = -1;

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }
}
