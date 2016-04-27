package com.thirdandloom.storyflow;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Theme {

    public static void loadRecources(Context context) {
        Stickers.catMapKeysList = Arrays.asList("[angry]", "[begging]", "[content]",
                "[cry]", "[dancing]", "[dejected]",
                "[dizzy]", "[happy]", "[ignoring]",
                "[indifferent]", "[lazy]", "[love]",
                "[mad]", "[perplexed]", "[puke]",
                "[puppy-eyes]", "[shy]", "[sleeping]",
                "[stary-eyes]", "[surprised]");

        Map<String, Drawable> map = new HashMap<>();
        map.put("[angry]", prepare(context.getResources().getDrawable(R.drawable.angry)));
        map.put("[begging]", prepare(context.getResources().getDrawable(R.drawable.begging)));
        map.put("[content]", prepare(context.getResources().getDrawable(R.drawable.content)));
        map.put("[cry]", prepare(context.getResources().getDrawable(R.drawable.cry)));
        map.put("[dancing]", prepare(context.getResources().getDrawable(R.drawable.dancing)));
        map.put("[dejected]", prepare(context.getResources().getDrawable(R.drawable.dejected)));
        map.put("[dizzy]", prepare(context.getResources().getDrawable(R.drawable.dizzy)));
        map.put("[happy]", prepare(context.getResources().getDrawable(R.drawable.happy)));
        map.put("[ignoring]", prepare(context.getResources().getDrawable(R.drawable.ignoring)));
        map.put("[indifferent]", prepare(context.getResources().getDrawable(R.drawable.indifferent)));
        map.put("[lazy]", prepare(context.getResources().getDrawable(R.drawable.lazy)));
        map.put("[love]", prepare(context.getResources().getDrawable(R.drawable.love)));
        map.put("[mad]", prepare(context.getResources().getDrawable(R.drawable.mad)));
        map.put("[perplexed]", prepare(context.getResources().getDrawable(R.drawable.perplexed)));
        map.put("[puke]", prepare(context.getResources().getDrawable(R.drawable.puke)));
        map.put("[puppy-eyes]", prepare(context.getResources().getDrawable(R.drawable.puppy_eyes)));
        map.put("[shy]", prepare(context.getResources().getDrawable(R.drawable.shy)));
        map.put("[sleeping]", prepare(context.getResources().getDrawable(R.drawable.sleeping)));
        map.put("[stary-eyes]", prepare(context.getResources().getDrawable(R.drawable.stary_eyes)));
        map.put("[surprised]", prepare(context.getResources().getDrawable(R.drawable.surprised)));
        Stickers.catMap = Collections.unmodifiableMap(map);

    }

    public static class Stickers {
        public static Map<String, Drawable> catMap;
        public static List<String> catMapKeysList;
        //...
    }

    private static Drawable prepare(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }
}
