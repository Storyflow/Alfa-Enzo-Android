package com.thirdandloom.storyflow.utils.image;

import com.thirdandloom.storyflow.utils.DeviceUtils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public class CropTakenPhotoBitmapProcessor extends BitmapProcessor {

    public CropTakenPhotoBitmapProcessor(@NonNull Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    protected Bitmap getProcessedBitmap() {
        width = DeviceUtils.getDisplayWidth();
        height = DeviceUtils.getDisplayHeight();
        x = (bitmap.getWidth() - width) / 2;
        y = (bitmap.getHeight() - height) / 2;
        return super.getProcessedBitmap();
    }

    @Override
    protected boolean isIdentity() {
        return false;
    }

    @Override
    protected boolean isResizeNeeded() {
        return Math.max(bitmap.getHeight(), bitmap.getWidth()) > Math.max(maxSize.height(), maxSize.width())
                || Math.min(bitmap.getHeight(), bitmap.getWidth()) > Math.min(maxSize.height(), maxSize.width());
    }
}
