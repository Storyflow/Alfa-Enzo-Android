package com.thirdandloom.storyflow.adapters;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StoriesPreviewAdapter extends RecyclerView.Adapter<StoriesPreviewAdapter.StoryContentHolder> {

    private Context context;

    public StoriesPreviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public StoryContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_story_content, parent, false);
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
        //return 0;
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
