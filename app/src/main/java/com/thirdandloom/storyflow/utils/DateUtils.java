package com.thirdandloom.storyflow.utils;

import com.thirdandloom.storyflow.R;
import rx.functions.Action2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtils extends BaseUtils {
    private static final int NOW = 0;
    private static final int NOW_NEXT = NOW + 1;
    private static final int NOW_PREVIOUS = NOW - 1;

    static final Map<Integer, Integer> monthMap;

    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(Calendar.JANUARY, R.string.january);
        map.put(Calendar.FEBRUARY, R.string.february);
        map.put(Calendar.MARCH, R.string.march);
        map.put(Calendar.APRIL, R.string.april);
        map.put(Calendar.MAY, R.string.may);
        map.put(Calendar.JUNE, R.string.june);
        map.put(Calendar.JULY, R.string.july);
        map.put(Calendar.AUGUST, R.string.august);
        map.put(Calendar.SEPTEMBER, R.string.september);
        map.put(Calendar.OCTOBER, R.string.october);
        map.put(Calendar.NOVEMBER, R.string.november);
        map.put(Calendar.DECEMBER, R.string.december);
        monthMap = Collections.unmodifiableMap(map);
    }

    static final Map<Integer, Integer> weekDayMap;

    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(Calendar.MONDAY, R.string.monday);
        map.put(Calendar.TUESDAY, R.string.tuesday);
        map.put(Calendar.WEDNESDAY, R.string.wednesday);
        map.put(Calendar.THURSDAY, R.string.thursday);
        map.put(Calendar.FRIDAY, R.string.friday);
        map.put(Calendar.SATURDAY, R.string.saturday);
        map.put(Calendar.SUNDAY, R.string.sunday);
        weekDayMap = Collections.unmodifiableMap(map);
    }

    static final Map<Integer, Integer> monthRelativeMap;

    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(NOW, R.string.this_month);
        map.put(NOW_NEXT, R.string.next_month);
        map.put(NOW_PREVIOUS, R.string.previous_month);
        monthRelativeMap = Collections.unmodifiableMap(map);
    }

    static final Map<Integer, Integer> yearRelativeMap;

    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(NOW, R.string.this_year);
        map.put(NOW_NEXT, R.string.next_year);
        map.put(NOW_PREVIOUS, R.string.last_year);
        yearRelativeMap = Collections.unmodifiableMap(map);
    }

    static final Map<Integer, Integer> dayRelativeMap;

    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(NOW, R.string.today);
        map.put(NOW_NEXT, R.string.tomorrow);
        map.put(NOW_PREVIOUS, R.string.yesterday);
        dayRelativeMap = Collections.unmodifiableMap(map);
    }

    public static Calendar todayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static void getMonthlyRepresentation(Calendar checkCalendar, Action2<String, String> representation) {
        representation(checkCalendar, Calendar.MONTH, monthRelativeMap, monthMap, "yyyy", representation);
    }

    public static void getYearlyRepresentation(Calendar checkCalendar, Action2<String, String> representation) {
        representation(checkCalendar, Calendar.YEAR, yearRelativeMap, null, "yyyy", representation);
    }

    public static void getDailyRepresentation(Calendar checkCalendar, Action2<String, String> representation) {
        representation(checkCalendar, Calendar.DAY_OF_WEEK, dayRelativeMap, weekDayMap, "MM.dd.yyyy", representation);
    }

    private static void representation(Calendar checkCalendar, int field, Map<Integer, Integer> relativeMap, Map<Integer, Integer> resourcesMap, String dateFormatPattern, Action2<String, String> representation) {
        Calendar todayCalendar = todayCalendar();
        int now = todayCalendar.get(field);
        int checked = checkCalendar.get(field);
        int deltaMonth = checked - now;

        String boldText;
        if (same(todayCalendar, checkCalendar) && relativeMap.containsKey(deltaMonth)) {
            boldText = getResources().getString(relativeMap.get(deltaMonth));
        } else if (resourcesMap != null) {
            boldText = getResources().getString(resourcesMap.get(checkCalendar.get(field)));
        } else {
            boldText = String.valueOf(checked);
        }

        representation.call(boldText, getDateString(dateFormatPattern, checkCalendar.getTime()));
    }

    private static boolean same(Calendar todayCalendar, Calendar checkCalendar) {
        return todayCalendar.get(Calendar.YEAR) == checkCalendar.get(Calendar.YEAR)
                && todayCalendar.get(Calendar.DAY_OF_YEAR) == checkCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public static String getDateString(String pattern, Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);
        return dateFormatter.format(date);
    }
}
