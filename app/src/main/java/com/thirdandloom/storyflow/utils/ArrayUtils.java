package com.thirdandloom.storyflow.utils;

import rx.functions.Action2;
import rx.functions.Func2;

import java.util.List;

public class ArrayUtils extends BaseUtils {
    public static final int INDEX_NOT_FOUND = -1;
    public static final int EMPTY_POSITION = -1;

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static <T> void forEach(List<T> list, Func2<T, Integer, Boolean> interaction) {
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            if (!interaction.call(item, i)) break;
        }
    }

    public static <T> void forEach(List<T> list, Action2<T, Integer> interaction) {
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            interaction.call(item, i);
        }
    }
}
