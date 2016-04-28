package com.thirdandloom.storyflow.views.emoji;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.Theme;
import rx.functions.Action1;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CatsStickersView extends FrameLayout {

    public CatsStickersView(Context context) {
        this(context, null);
    }

    public CatsStickersView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CatsStickersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private RecyclerView iconsGridView;
    private Action1<String> onStickerSelected;

    public void setOnStickerSelected(Action1<String> onStickerSelected) {
        this.onStickerSelected = onStickerSelected;
    }

    private void init() {
        inflate(getContext(), R.layout.view_cats_emotiicons, this);

        iconsGridView = (RecyclerView)findViewById(R.id.view_cats_emotiicons_grid);
        initIconsGridView();
    }

    private void initIconsGridView() {
        iconsGridView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 5, GridLayoutManager.VERTICAL, false);
        iconsGridView.setLayoutManager(layoutManager);
        iconsGridView.setAdapter(new CatsEmotiIconsAdapter());
    }

    private class CatsEmotiIconsAdapter extends RecyclerView.Adapter<EmotiIconHolder> {

        @Override
        public EmotiIconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_emotiicon, parent, false);
            EmotiIconHolder holder = new EmotiIconHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(EmotiIconHolder holder, int position) {
            String emotiKey = Theme.Stickers.catMapKeysList.get(position);
            Drawable imageDrawable = Theme.Stickers.catMap.get(emotiKey);
            holder.setData(imageDrawable, emotiKey);
        }

        @Override
        public int getItemCount() {
            return Theme.Stickers.catMapKeysList.size();
        }
    }

    public class EmotiIconHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        String emotiKey;

        public EmotiIconHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.adapter_recycler_item_emotiicon_image_view);
            this.imageView.setOnClickListener(v -> {
                onStickerSelected.call(emotiKey);
            });
        }

        public void setData(Drawable drawable, String emotiKey) {
            this.imageView.setImageDrawable(drawable);
            this.emotiKey = emotiKey;
        }
    }
}
