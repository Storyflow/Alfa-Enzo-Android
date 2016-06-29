package com.thirdandloom.storyflow.utils.image;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.adapters.BrowsePeriodsAdapter;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.BaseUtils;
import com.thirdandloom.storyflow.utils.DateUtils;
import com.thirdandloom.storyflow.utils.MathUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.concurrent.BackgroundRunnable;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class StoryflowImageUtils extends BaseUtils {

    public static class Config {
        private Context context;
        private ImageView imageView;
        private Story story;
        private BrowsePeriodsAdapter.ItemType itemType = BrowsePeriodsAdapter.ItemType.Large;
        private int itemWidthPx;

        public static Config with(Context context, ImageView imageView) {
            Config config = new Config();
            config.context = context;
            config.imageView = imageView;
            return config;
        }

        public Config story(Story story) {
            this.story = story;
            return this;
        }

        public Config itemType(BrowsePeriodsAdapter.ItemType itemType) {
            this.itemType = itemType;
            return this;
        }

        public Config itemWidthPx(int itemWidthPx) {
            this.itemWidthPx = itemWidthPx;
            return this;
        }

        public int showText() {
            int imageHeight;
            int imageWidth;
            String imageUrl = story.getAuthor().getCroppedImageCover().getImageUrl();
            switch (itemType) {
                case Smallest:
                    imageHeight = AndroidUtils.dp(50);
                    break;
                case Small:
                    imageHeight = AndroidUtils.dp(100);
                    break;
                case Large:
                    imageHeight = AndroidUtils.dp(150);
                    break;
                default:
                    imageHeight = AndroidUtils.dp(150);
            }
            imageWidth = itemWidthPx;
            int height = calculateHeight(imageWidth, imageHeight, itemWidthPx);
            ViewUtils.applyHeight(imageView, height);
            Glide
                    .with(context)
                    .load(imageUrl)
                    .override(itemWidthPx, height)
                    .placeholder(R.color.yellow)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            return height;
        }

        public int showImage() {
            String imageUrl;
            int imageHeight;
            int imageWidth;
            switch (itemType) {
                case Small:
                case Smallest:
                default:
                    imageUrl = story.getImageData().getCollapsedSizedImage().url();
                    imageHeight = story.getImageData().getCollapsedSizedImage().size().height();
                    imageWidth = story.getImageData().getCollapsedSizedImage().size().width();
                    break;
                case Large:
                    imageUrl = story.getImageData().getNormalSizedImage().url();
                    imageHeight = story.getImageData().getNormalSizedImage().size().height();
                    imageWidth = story.getImageData().getNormalSizedImage().size().width();
                    break;
            }

            //TODO
            //this code should be removed after story.getImageData().getNormalSizedImage().size()
            //fixed: story.getImageData().getNormalSizedImage().size() = (0, 0)
            if (imageHeight == 0 || imageWidth == 0) {
                imageHeight = AndroidUtils.dp(100);
                imageWidth = itemWidthPx;
            }

            int height = calculateHeight(imageWidth, imageHeight, itemWidthPx);
            ViewUtils.applyHeight(imageView, height);
            Glide
                    .with(context)
                    .load(imageUrl)
                    .override(itemWidthPx, height)
                    .placeholder(R.color.yellow)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            return height;
        }
    }

    public static void saveImage(Context context, String imageUrl) {
        Glide
            .with(context)
            .load(imageUrl)
            .asBitmap()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .fitCenter().atMost().override(2500, 2500)
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    StoryflowApplication.runBackground(new BackgroundRunnable() {
                        @Override
                        public void run() {
                            super.run();
                            insertImage(getApplicationContext().getContentResolver(), resource, PhotoFileUtils.generateImageUniqueName());
                        }
                    });
                }
            });
    }

    private static int calculateHeight(int realWidth, int realHeight, int boxWidth) {
        float coef = MathUtils.calculateMaxScaleRatio(realWidth, realHeight, boxWidth);
        return Math.round(coef * realHeight);
    }

    /**
     * A copy of the Android internals  insertImage method, this method populates the
     * meta data with DATE_ADDED and DATE_TAKEN. This fixes a common problem where media
     * that is inserted manually gets saved at the end of the gallery (because date is not populated).
     * @see android.provider.MediaStore.Images.Media#insertImage(ContentResolver, Bitmap, String, String)
     */
    public static String insertImage(ContentResolver cr,
            Bitmap source,
            String title) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery

        double currentTimeSec = DateUtils.millisToSeconds(System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_ADDED, currentTimeSec);
        values.put(MediaStore.Images.Media.DATE_TAKEN, currentTimeSec);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, currentTimeSec);

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    /**
     * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
     * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
     * meta data. The StoreThumbnail method is private so it must be duplicated here.
     * @see android.provider.MediaStore.Images.Media (StoreThumbnail private method)
     */
    @Nullable
    private static Bitmap storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind) {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND,kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID,(int)id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT,thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH,thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            exception(ex);
            return null;
        } catch (IOException ex) {
            exception(ex);
            return null;
        }
    }
}
