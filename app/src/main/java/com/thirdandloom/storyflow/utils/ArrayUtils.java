package com.thirdandloom.storyflow.utils;

import java.util.List;

public class ArrayUtils {
    public static final int INDEX_NOT_FOUND = -1;

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }
}
