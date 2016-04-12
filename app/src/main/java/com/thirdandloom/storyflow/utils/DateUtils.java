package com.thirdandloom.storyflow.utils;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.utils.models.Time;

import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DateUtils extends BaseUtils {
    public static final long MY_IN_MS = 1000;
    public static final long MS_IN_SEC = 1000;
    public static final long SEC_IN_MIN = 60;
    public static final long MIN_IN_HOUR = 60;
    public static final long HOUR_IN_DAY = 24;
    public static final long DAY_IN_WEEK = 7;
    public static final long MS_IN_MIN = SEC_IN_MIN * MS_IN_SEC;
    public static final long MS_IN_HOUR = MIN_IN_HOUR * MS_IN_MIN;
    public static final long SEC_IN_HOUR = MIN_IN_HOUR * SEC_IN_MIN;
    public static final long MS_IN_DAY = HOUR_IN_DAY * MS_IN_HOUR;
    public static final long SEC_IN_DAY = HOUR_IN_DAY * SEC_IN_HOUR;
    public static final long MS_IN_WEEK = DAY_IN_WEEK * MS_IN_DAY;

    public static final String DATE_FRIENDLY_FORMAT = "MMM d, yyyy";
    public static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String HH_MM = "HH:mm";
    public static final String HH_MM_AM_PM = "h:mm a";
    public static final String HH_MM_SS = "HH\nmm:ss";

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
    public static String getTimezoneId() {
        return TimeZone.getDefault().getID();
    }

    public static double millisToSeconds(long milliseconds) {
        return milliseconds / MS_IN_SEC;
    }

    public static void getMonthlyRepresentation(Calendar checkCalendar, Action2<String, String> representation) {
        Calendar todayCalendar = todayCalendar();

        Time todayTime = new Time(todayCalendar);
        Time compareTime = new Time(checkCalendar);

        String boldText;
        if (compareTime.getMonthStart().equals(todayTime.getMonthStart())) {
            boldText = getResources().getString(monthRelativeMap.get(NOW));
        } else if (compareTime.getNextMonthDayStart().equals(todayTime.getMonthStart())) {
            boldText = getResources().getString(monthRelativeMap.get(NOW_PREVIOUS));
        } else if (todayTime.getNextMonthDayStart().equals(compareTime.getMonthStart())) {
            boldText = getResources().getString(monthRelativeMap.get(NOW_NEXT));
        } else {
            boldText = getResources().getString(monthMap.get(checkCalendar.get(Calendar.MONTH)));
        }

        String formattedDate = getDateString("yyyy", checkCalendar.getTime());
        representation.call(boldText, formattedDate);
    }

    public static void getYearlyRepresentation(Calendar checkCalendar, Action2<String, String> representation) {
        Calendar todayCalendar = todayCalendar();

        Time todayTime = new Time(todayCalendar);
        Time compareTime = new Time(checkCalendar);

        String boldText;
        if (compareTime.getYearStart().equals(todayTime.getYearStart())) {
            boldText = getResources().getString(yearRelativeMap.get(NOW));
        } else if (compareTime.getNextYearStart().equals(todayTime.getYearStart())) {
            boldText = getResources().getString(yearRelativeMap.get(NOW_PREVIOUS));
        } else if (todayTime.getNextYearStart().equals(compareTime.getYearStart())) {
            boldText = getResources().getString(yearRelativeMap.get(NOW_NEXT));
        } else {
            boldText = String.valueOf(checkCalendar.get(Calendar.YEAR));
        }

        String formattedDate = getDateString("yyyy", checkCalendar.getTime());
        representation.call(boldText, formattedDate);
    }

    public static void getDailyRepresentation(Calendar checkCalendar, Action2<String, String> representation) {
        Calendar todayCalendar = todayCalendar();

        Time todayTime = new Time(todayCalendar);
        Time compareTime = new Time(checkCalendar);

        String boldText;
        if (compareTime.getDayStart().equals(todayTime.getDayStart())) {
            boldText = getResources().getString(dayRelativeMap.get(NOW));
        } else if (compareTime.getNextDayStart().equals(todayTime.getDayStart())) {
            boldText = getResources().getString(dayRelativeMap.get(NOW_PREVIOUS));
        } else if (todayTime.getNextDayStart().equals(compareTime.getDayStart())) {
            boldText = getResources().getString(dayRelativeMap.get(NOW_NEXT));
        } else {
            boldText = getResources().getString(weekDayMap.get(checkCalendar.get(Calendar.DAY_OF_WEEK)));
        }

        String formattedDate = getDateString("MM.dd.yyyy", checkCalendar.getTime());
        representation.call(boldText, formattedDate);
    }

    public static String getDateString(String pattern, Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);
        return dateFormatter.format(date);
    }
}
