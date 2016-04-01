package com.thirdandloom.storyflow.utils.glide;

/**
 * Copyright (C) 2015 Wasabeef
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.util.UUID;

public class CropRectTransformation implements Transformation<Bitmap> {

    private BitmapPool bitmapPool;
    private RectF destinationRect;

    public CropRectTransformation(Context context, RectF destinationRectF) {
        this(Glide.get(context).getBitmapPool(), destinationRectF);
    }

    public CropRectTransformation(BitmapPool pool, RectF destinationRectF) {
        this.bitmapPool = pool;
        this.destinationRect = destinationRectF;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();
        int width = (int) destinationRect.width();
        int height = (int) destinationRect.height();
        int x = (int) destinationRect.left;
        int y = (int) destinationRect.top;
        Bitmap bitmap = Bitmap.createBitmap(source, x, y, width, height);

        return BitmapResource.obtain(bitmap, bitmapPool);
    }

    @Override
    public String getId() {
        return "CropTransformation(width=" + destinationRect.width()
                + ", height=" + destinationRect.height()
                + ", left=" + destinationRect.left
                + ", right=" + destinationRect.right
                + ", bottom=" + destinationRect.bottom
                + ", top=" + destinationRect.top
                + ")";
    }

}
