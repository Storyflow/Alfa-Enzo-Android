package com.thirdandloom.storyflow.managers;

import com.thirdandloom.storyflow.models.Story;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StoriesManager {

    private RequestData requestData = new RequestData();
    private LinkedHashMap<Calendar, Story.WrapList> store = new LinkedHashMap<>();
    private List<Integer> fetchedPositions = new LinkedList<>();

    public StoriesManager() {

    }

    public RequestData getRequestData() {
        return requestData;
    }

    public RequestData getRequestData(Calendar calendar) {
        requestData.setDate(calendar.getTime());
        return requestData;
    }

    public void addFetchedStoryPosition(Integer position) {
        fetchedPositions.add(position);
    }

    public boolean isFetchedPosition(Integer position) {
        return fetchedPositions.contains(position);
    }

    public void removeFromFetchedPositions(Integer position) {
        fetchedPositions.remove(position);
    }

    @Nullable
    public Story.WrapList getStories(@NonNull Calendar calendar) {
        return store.containsKey(calendar)
                ? store.get(calendar)
                : null;
    }

    public void storeData(Calendar calendar, Story.WrapList list) {
        store.put(calendar, list);
    }

    public void clearStore() {
        fetchedPositions.clear();
        store.clear();
    }

    public static class RequestData {
        private int limit = 20;
        private int period = Period.Year|Period.Month|Period.Day;
        private int owners = Owners.Me|Owners.Followings|Owners.Friends;
        private int direction = Direction.None;
        private Date date = new Date();

        public void selectPeriodYearly() {
            period = Period.Year;
        }

        public void selectPeriodDaily() {
            period = Period.Year|Period.Month|Period.Day;
        }

        public void selectPeriodMonthly() {
            period = Period.Year|Period.Month;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public int getLimit() {
            return limit;
        }

        public String getDirection() {
            return Direction.directionMap.get(direction);
        }

        public String getPeriod() {
            String stringFormat = Period.dateFormats.get(period);
            DateFormat dateFormat = new SimpleDateFormat(stringFormat);
            return dateFormat.format(date);
        }

        public Map getFilters() {
            Map<String, String> filters = new IdentityHashMap<>();
            for (Integer owner : Owners.list) {
                String filter = Owners.ownersMap.get(owners & owner);
                if (filter != null) {
                    filters.put(new String("filters[]"), filter);
                }
            }

            return filters;
        }

        static class Period {
            static final int Year = 0x0001;
            static final int Month = Year << 1;
            static final int Day = Month << 1;
            static final Map<Integer, String> dateFormats;
            static {
                Map<Integer, String> map = new HashMap<>();
                map.put(Year, "yyyy");
                map.put(Month|Year, "yyyy/MM");
                map.put(Day|Month|Year, "yyyy/MM/dd");
                dateFormats = Collections.unmodifiableMap(map);
            }
        }

        static class Owners {
            static final int Me =  0x0001;
            static final int Friends = Me << 1;
            static final int Followings = Friends << 1;
            static final Map<Integer, String> ownersMap;
            static {
                Map<Integer, String> map = new HashMap<>();
                map.put(Me, "me");
                map.put(Friends, "friends");
                map.put(Followings, "followings");
                ownersMap = Collections.unmodifiableMap(map);
            }
            static final List<Integer> list = Arrays.asList(Me, Friends, Followings);
        }

        static class Direction {
            static final int None =  0x0001;
            static final int Forward =  None << 1;
            static final int Backward = Forward << 1;
            static final Map<Integer, String> directionMap;
            static {
                Map<Integer, String> map = new HashMap<>();
                map.put(Forward, "forward");
                map.put(Backward, "backward");
                directionMap = Collections.unmodifiableMap(map);
            }
        }
    }
}
