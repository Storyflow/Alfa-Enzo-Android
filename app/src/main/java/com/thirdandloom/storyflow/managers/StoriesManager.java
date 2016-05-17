package com.thirdandloom.storyflow.managers;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.DateUtils;
import rx.functions.Action3;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public StoriesManager(@Nullable LinkedHashMap<Calendar, Story.WrapList> store, @Nullable List<Integer> fetchedPositions, @Nullable RequestData requestData) {
        if (store != null && fetchedPositions != null && requestData != null) {
            this.store = store;
            this.fetchedPositions = fetchedPositions;
            this.requestData = requestData;
        }
    }

    public void getSavedData(Action3<LinkedHashMap<Calendar, Story.WrapList>, List<Integer>, RequestData> callback) {
        callback.call(store, fetchedPositions, requestData);
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
        List<Story> storiesForDate = StoryflowApplication.getPendingStoriesManager().getStories(calendar, getRequestData().getPeriodType());
        Story.WrapList storiesWrapList = new Story.WrapList();
        storiesWrapList.addStories(storiesForDate);

        if (!store.containsKey(calendar) && storiesWrapList.getStories().isEmpty()) {
            return null;
        } else if (store.containsKey(calendar)) {
            Story.WrapList storedStories = store.get(calendar);
            storiesWrapList.addStories(storedStories.getStories());
            storiesWrapList.setNextStoryStartDate(storedStories.getNextStoryStartDate());
            storiesWrapList.setPreviousStoryStartDate(storedStories.getPreviousStoryStartDate());
        }
        return storiesWrapList;
    }

    public void storeData(Calendar calendar, Story.WrapList list) {
        store.put(calendar, list);
    }

    public void clearStore() {
        fetchedPositions.clear();
        store.clear();
    }

    public static class RequestData implements Serializable {
        private static final long serialVersionUID = 292337049982264779L;

        private int limit = 20;
        private Period.Type period = Period.Type.Daily;
        private int owners = Owners.Me|Owners.Followings|Owners.Friends;
        private Direction.Type direction = Direction.Type.None;
        private Date date = new Date();

        public void selectPeriodYearly() {
            period = Period.Type.Yearly;
        }

        public void selectPeriodDaily() {
            period = Period.Type.Daily;
        }

        public void selectPeriodMonthly() {
            period = Period.Type.Monthly;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public int getLimit() {
            return limit;
        }

        public String getDirection() {
            return Direction.directionMap.get(direction);
        }

        public Period.Type getPeriodType() {
            return period;
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

        public static class Period {
            public enum Type {
                Yearly, Monthly, Daily
            }
            static final Map<Type, String> dateFormats;
            static {
                Map<Type, String> map = new HashMap<>();
                map.put(Type.Yearly, "yyyy");
                map.put(Type.Monthly, "yyyy/MM");
                map.put(Type.Daily, "yyyy/MM/dd");
                dateFormats = Collections.unmodifiableMap(map);
            }
        }

        public static class Owners {
            public static final int Me =  0x0001;
            public static final int Friends = Me << 1;
            public static final int Followings = Friends << 1;
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

        public static class Direction {
            public enum Type {
                None, Forward, Backward
            }
            static final Map<Type, String> directionMap;
            static {
                Map<Type, String> map = new HashMap<>();
                map.put(Type.Forward, "forward");
                map.put(Type.Backward, "backward");
                directionMap = Collections.unmodifiableMap(map);
            }
        }
    }
}
