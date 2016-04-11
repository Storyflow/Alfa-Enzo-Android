package com.thirdandloom.storyflow.adapters;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.DateUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import rx.functions.Action3;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.StoryHolder> {

    public enum ItemWidth {
        Large, Small
    }

    public enum ItemType {
        Daily, Monthly, Yearly
    }

    private ItemWidth itemWidth = ItemWidth.Large;
    private ItemType itemType = ItemType.Daily;
    private Action3<Integer, Integer, View> onVerticalScroll;
    private Context context;
    private int centerPosition;

    public HorizontalRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public ItemWidth getItemWidth() {
        return itemWidth;
    }

    public void setCenterPosition(int centerPosition) {
        this.centerPosition = centerPosition;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public void changeItemWidth() {
        switch (itemWidth) {
            case Large:
                itemWidth = ItemWidth.Small;
                break;
            case Small:
                itemWidth = ItemWidth.Large;
                break;
        }
    }

    public static int getItemMargin() {
        return StoryflowApplication.resources().getDimensionPixelOffset(R.dimen.sizeTiny);
    }

    public int getItemWidthPixel() {
        switch (itemWidth) {
            case Large:
                int itemPadding = StoryflowApplication.resources().getDimensionPixelOffset(R.dimen.sizeNormal);
                return DeviceUtils.getDisplayWidth()-itemPadding*2;
            case Small:
                return DeviceUtils.getDisplayWidth()/2;
        }

        throw new UnsupportedOperationException("unsupported itemWidth is using");
    }

    public void onVerticalScrollChanged(Action3<Integer, Integer, View> onScroll) {
        this.onVerticalScroll = onScroll;
    }

    @Override
    public StoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_main_horizontal, parent, false);
        StoryHolder storyHolder = new StoryHolder(v);
        storyHolder.onVerticalScrollChanged(onVerticalScroll);
        return storyHolder;
    }

    @Override
    public void onBindViewHolder(StoryHolder storyHolder, int position) {
        ViewUtils.applyWidth(storyHolder.itemView, getItemWidthPixel());
        updateDate(storyHolder, position);

        RecyclerView.Adapter<StoryContentHolder> adapter = new RecyclerView.Adapter<StoryContentHolder>() {
            @Override
            public StoryContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_horizontal_story, parent, false);
                StoryContentHolder holder = new StoryContentHolder(v);
                return holder;
            }

            @Override
            public void onBindViewHolder(StoryContentHolder holder, int position) {
                Random random = new Random();
                String imageUrl = testImages.get(random.nextInt(testImages.size()));

                Glide.with(context).load(imageUrl).into(holder.imageView);
                holder.textView.setText(imageUrl);
            }

            @Override
            public int getItemCount() {
                return testImages.size();
            }
        };
        storyHolder.recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    private void updateDate(StoryHolder storyHolder, int position) {
        Calendar calendar = DateUtils.todayCalendar();
        int offset = position - centerPosition;
        switch (itemType) {
            case Daily:
                calendar.add(Calendar.DAY_OF_YEAR, offset);
                DateUtils.getDailyRepresentation(calendar, storyHolder::setDateRepresentation);
                break;
            case Monthly:
                calendar.add(Calendar.MONTH, offset);
                DateUtils.getMonthlyRepresentation(calendar, storyHolder::setDateRepresentation);
                break;
            case Yearly:
                calendar.add(Calendar.YEAR, offset);
                DateUtils.getYearlyRepresentation(calendar, storyHolder::setDateRepresentation);
                break;
            default:
                throw new UnsupportedOperationException("unsupported itemType is using");
        }

    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public static class StoryHolder extends RecyclerView.ViewHolder {

        private TextView dateTextView;
        private TextView boldDateTextView;
        private RecyclerView recyclerView;

        private Action3<Integer, Integer, View> onVerticalScroll;

        public StoryHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_text_view);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_recycler_view);
            boldDateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_bold_text_view);
            SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)itemView.findViewById(R.id.adapter_recycler_item_horizontal_refresh_layout);
            refreshLayout.setColorSchemeResources(R.color.yellow, R.color.grey);
            refreshLayout.setOnRefreshListener(() -> {
                refreshLayout.setRefreshing(false);
            });

            LinearLayoutManager manager = new LinearLayoutManager(itemView.getContext());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(manager);
            initListeners();
        }

        private int scrollPosition;
        private void initListeners() {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    scrollPosition += dy;
                    if (onVerticalScroll != null) onVerticalScroll.call(scrollPosition, dy, StoryHolder.this.itemView);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }

        public void onVerticalScrollChanged(Action3<Integer, Integer, View> onScroll) {
            this.onVerticalScroll = onScroll;
        }

        public void setDateRepresentation(String boldText, String formattedDate) {
            dateTextView.setText(formattedDate);
            boldDateTextView.setText(boldText);
        }
    }

    public static class StoryContentHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        public StoryContentHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_text_view);
            imageView = (ImageView)itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_image_view);
        }
    }

    private static Date getDateForPosition(int position, int centerPosition, ItemType itemType) {
        Calendar calendar = DateUtils.todayCalendar();
        int offset = position - centerPosition;
        switch (itemType) {
            case Daily:
                calendar.add(Calendar.DAY_OF_YEAR, offset);
                break;
            case Monthly:
                calendar.add(Calendar.MONTH, offset);
                break;
            case Yearly:
                calendar.add(Calendar.YEAR, offset);
                break;
            default:
                throw new UnsupportedOperationException("unsupported itemType is using");
        }

        return calendar.getTime();
    }


    private static final List<String> testImages = Arrays.asList(
            "http://www.keenthemes.com/preview/metronic/theme/assets/global/plugins/jcrop/demos/demo_files/image1.jpg",
            "http://www.keenthemes.com/preview/conquer/assets/plugins/jcrop/demos/demo_files/image2.jpg",
            "http://wowslider.com/sliders/demo-85/data1/images/southtyrol350698.jpg",
            "http://www.spacew.com/gallery/image006169.jpg",
            "http://macroclub.ru/gallery/data/552/IMGP0171_1.jpg",
            "http://ichef.bbci.co.uk/wwfeatures/624_351/images/live/p0/3n/x3/p03nx374.jpg",
            "http://wpinprogress.com/demo/voobis/wp-content/uploads/2012/11/image.jpg",
            "http://www.maisonducolombier.com/images/portfolio/lieu/image-20.jpg",
            "http://fotoshkola.net/system/my_photos/0002/1144/image-normal.jpg",
            "https://assets.answersingenesis.org/img/cms/content/contentnode/image/what-the-image-of-god-isnt.jpg",
            "http://news.ponycanyon.co.jp/wp/wp-content/uploads/2016/02/image-2-320x316.jpeg",
            "http://s19.postimg.org/ypsrfx6cz/image.jpg");
}
