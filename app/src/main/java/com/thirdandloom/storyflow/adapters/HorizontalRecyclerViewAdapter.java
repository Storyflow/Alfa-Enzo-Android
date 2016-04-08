package com.thirdandloom.storyflow.adapters;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.ColorUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import rx.functions.Action3;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.StoryHolder> {

    public enum ItemWidth {
        Large, Small
    }

    private ItemWidth itemWidth = ItemWidth.Large;
    private Action3<Integer, Integer, View> onVerticalScroll;

    public HorizontalRecyclerViewAdapter() {

    }

    public ItemWidth getItemWidth() {
        return itemWidth;
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

    public static int getItemMarging() {
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

        return 0;
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
        View itemView = storyHolder.itemView;
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.width = getItemWidthPixel();
        itemView.setLayoutParams(params);

        storyHolder.textView.setText(String.format("%d", position));

        RecyclerView.Adapter<StoryContentHolder> adapter = new RecyclerView.Adapter<StoryContentHolder>() {
            @Override
            public StoryContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_horizontal_story, parent, false);
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.width = getItemWidthPixel();
                v.setLayoutParams(params);
                StoryContentHolder holder = new StoryContentHolder(v);
                return holder;
            }

            @Override
            public void onBindViewHolder(StoryContentHolder holder, int position) {
                View itemView = holder.itemView;
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.width = getItemWidthPixel();
                itemView.setLayoutParams(params);

                Random random = new Random();
                String imageUrl = testImages.get(random.nextInt(testImages.size()));

                Glide.with(StoryflowApplication.getInstance()).load(imageUrl).into(holder.imageView);
            }

            @Override
            public int getItemCount() {
                return testImages.size();
            }
        };
        storyHolder.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public static class StoryHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private RecyclerView recyclerView;

        private Action3<Integer, Integer, View> onVerticalScroll;

        public StoryHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_text_view);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_item_recyclerview);
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

    private static final List<String> testImages = Arrays.asList("http://www.keenthemes.com/preview/metronic/theme/assets/global/plugins/jcrop/demos/demo_files/image1.jpg",
            "http://www.keenthemes.com/preview/conquer/assets/plugins/jcrop/demos/demo_files/image2.jpg",
            "http://7-themes.com/data_images/out/14/6817018-image.jpg",
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
