package com.thirdandloom.storyflow.utils.models;

import com.thirdandloom.storyflow.utils.DateUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Time implements Serializable {

    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * integer part represents seconds
     * fraction part represents millis and microseconds
     */
    private double value;

    public Time() {
        value = 0;
    }

    public Time(Time time) {
        this(time.getPrecise());
    }

    public Time(long millis) {
        value = DateUtils.millisToSeconds(millis);
    }

    public Time(double precise) {
        value = precise;
    }

    public Time(Calendar calendar) {
        this(calendar.getTimeInMillis());
    }

    public Time(Date date) {
        this(date.getTime());
    }

    public double getPrecise() {
        return value;
    }

    /**
     * This operation loses time precision by rounding it from microseconds to milliseconds
     */
    public long roundToMillis() {
        long millis = (long) (value * DateUtils.MS_IN_SEC);
        return millis;
    }

    /**
     * This operation loses time precision by rounding it from microseconds to milliseconds
     */
    public Date convertToDate() {
        return new Date(roundToMillis());
    }

    @Override
    public String toString() {
        long roundedToMillis = roundToMillis();
        String timeWithMillisPrecision = TIME_FORMATTER.format(roundedToMillis);
        int microseconds = getMicroseconds();
        if (microseconds == 0) {
            return timeWithMillisPrecision;
        }
        return String.format("%s%d", timeWithMillisPrecision, microseconds);
    }

    public String toDecimalString() {
        String result = String.format(Locale.ENGLISH, "%.6f", new BigDecimal(value));
        return result;
    }

    private int getMicroseconds() {
        double wholePartIsMillis = value * DateUtils.MS_IN_SEC;
        double millisWithoutMicros = Math.floor(wholePartIsMillis);
        double microsInFraction = wholePartIsMillis - millisWithoutMicros;
        int wholePartIsMicros = (int) (microsInFraction * DateUtils.MY_IN_MS);
        return wholePartIsMicros;
    }

    private Calendar toCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(roundToMillis());
        return calendar;
    }

    public Time getYearStart() {
        Calendar calendar = toCalendar();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        return new Time(calendar);
    }

    public Time getNextYearStart() {
        Time dayStart = getYearStart();
        Calendar calendar = dayStart.toCalendar();
        calendar.add(Calendar.YEAR, 1);
        return new Time(calendar);
    }

    public Time getMonthStart() {
        Calendar calendar = toCalendar();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return new Time(calendar);
    }

    public Time getNextMonthDayStart() {
        Time dayStart = getMonthStart();
        Calendar calendar = dayStart.toCalendar();
        calendar.add(Calendar.MONTH, 1);
        return new Time(calendar);
    }

    public Time getDayStart() {
        Calendar calendar = toCalendar();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        return new Time(calendar);
    }

    public Time getNextDayStart() {
        Time dayStart = getDayStart();
        Calendar calendar = dayStart.toCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return new Time(calendar);
    }

    public Time subtractDays(int days) {
        Calendar calendar = toCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -1 * days);
        return new Time(calendar);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != Time.class) {
            return false;
        }
        Time oTime = (Time)o;
        return value == oTime.value;
    }

    @Override
    public int hashCode() {
        return new Double(value).hashCode();
    }
}
