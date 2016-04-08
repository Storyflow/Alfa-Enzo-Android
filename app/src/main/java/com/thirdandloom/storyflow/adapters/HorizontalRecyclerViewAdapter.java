package com.thirdandloom.storyflow.adapters;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import rx.functions.Action3;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.Holder> {

    public enum ItemWidth {
        Large, Small
    }

    private ItemWidth itemWidth = ItemWidth.Large;

    private static final int COUNT = 100;
    private List<Integer> items;
    private Context context;

    private Action3<Integer, Integer, View> onVerticalScroll;

    public HorizontalRecyclerViewAdapter(Context context) {
        items = new ArrayList<>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            items.add(i);
        }
        this.context = context;
    }

    public void setItemWidth(ItemWidth itemWidth) {
        this.itemWidth = itemWidth;
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
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_main_horizontal, parent, false);
        Holder holder = new Holder(v);
        holder.onVerticalScrollChanged(onVerticalScroll);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        View itemView = holder.itemView;
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.width = getItemWidthPixel();
        itemView.setLayoutParams(params);

        holder.textView.setText(String.format("%d", position));

        RecyclerView.Adapter<HorizontalRecyclerViewAdapter.SmallHolder> adapter = new RecyclerView.Adapter<HorizontalRecyclerViewAdapter.SmallHolder>() {
            @Override
            public HorizontalRecyclerViewAdapter.SmallHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_horizontal_story, parent, false);
                SmallHolder holder = new SmallHolder(v);
                return holder;
            }

            @Override
            public void onBindViewHolder(HorizontalRecyclerViewAdapter.SmallHolder holder, int position) {
                Integer number = items.get(position);
                holder.textView.setText(number.toString());
            }

            @Override
            public int getItemCount() {
                return items.size();
            }
        };
        holder.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private TextView textView;
        private RecyclerView recyclerView;

        private Action3<Integer, Integer, View> onVerticalScroll;

        public Holder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_view);
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
                    if (onVerticalScroll != null) onVerticalScroll.call(scrollPosition, dy, Holder.this.itemView);
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

    public static class SmallHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public SmallHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.text_view);
        }
    }
}
