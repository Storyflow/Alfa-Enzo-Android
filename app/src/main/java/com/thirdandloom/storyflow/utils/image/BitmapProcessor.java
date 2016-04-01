package com.thirdandloom.storyflow.utils.image;

import com.thirdandloom.storyflow.utils.MathUtils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BitmapProcessor {
    public static final int INVALID_ROTATE = 0;

    @NonNull
    protected Bitmap bitmap;

    private int rotation;
    private boolean mirror;
    private boolean recycle = true;
    private boolean crop = true;

    protected Size maxSize;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private Matrix matrix;

    public BitmapProcessor(@NonNull Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @NonNull
    public BitmapProcessor rotate(int rotation) {
        this.rotation = rotation;
        return this;
    }

    @NonNull
    public BitmapProcessor mirror() {
        mirror = true;
        return this;
    }

    @NonNull
    public BitmapProcessor resize(@Nullable Size maxSize, boolean needCrop) {
        this.maxSize = maxSize;
        crop = needCrop;
        return this;
    }

    @NonNull
    public BitmapProcessor notRecycle() {
        recycle = false;
        return this;
    }

    @NonNull
    public Bitmap process() {
        if (!isIdentity()) {
            processResize();
        }
        return bitmap;
    }

    private void processResize() {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        matrix = new Matrix();

        if (mirror) {
            matrix.preScale(-1, 1);
        }
        matrix.postRotate(rotation);
        if (crop) {
            calculateResize();
        } else {
            calculateResizeWithoutCrop();
        }
        bitmap = getProcessedBitmap();
    }

    protected boolean isIdentity() {
        return (rotation == INVALID_ROTATE) && !isResizeNeeded();
    }

    protected boolean isResizeNeeded() {
        return maxSize != null;
    }

    protected void calculateResize() {
        if (!isResizeNeeded()) {
            return;
        }
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int maxWidth = maxSize.width();
        int maxHeight = maxSize.height();
        if ((bitmapWidth == maxWidth) && (bitmapHeight == maxHeight)) {
            return;
        }

        float scaleFactor = MathUtils.calculateMaxScaleRatio(bitmapWidth, bitmapHeight, maxSize.width(), maxSize.height());
        Size outputSize = calculateOutputSize(maxSize, new Size(bitmapWidth, bitmapHeight), scaleFactor, getAspectRatio());
        x = (bitmapWidth - outputSize.width()) / 2;
        y = (bitmapHeight - outputSize.height()) / 2;
        width = outputSize.width();
        height = outputSize.height();

        if ((outputSize.width() != bitmapWidth) || (outputSize.height() != bitmapHeight)) {
            matrix.postScale(scaleFactor, scaleFactor);
        }
    }

    private void calculateResizeWithoutCrop() {
        if (isResizeNeeded()) {
            float scaleFactor = MathUtils.calculateMinScaleRatio(bitmap.getWidth(), bitmap.getHeight(), maxSize.width(), maxSize.height());
            matrix.postScale(scaleFactor, scaleFactor);
        }
    }

    private double getAspectRatio() {
        return (double) maxSize.width() / maxSize.height();
    }

    protected Bitmap getProcessedBitmap() {
        Bitmap processed = Bitmap.createBitmap(bitmap, x, y, width, height, matrix, true);
        if (recycle && !processed.equals(bitmap)) {
            bitmap.recycle();
        }
        return processed;
    }

    protected static Size calculateOutputSize(Size boxSize, Size realSize, float scaleFactor, double aspectRatio) {
        int width = calculateOutputSizeWithScale(boxSize.width(), scaleFactor, realSize.width());
        int height = calculateOutputSizeWithScale(boxSize.height(), scaleFactor, realSize.height());
        width = Math.min(width, (int) (height * aspectRatio));
        height = Math.min(height, (int) (width / aspectRatio));
        return new Size(width, height);
    }

    protected static int calculateOutputSizeWithScale(int maxSize, float scaleFactor, int currentSize) {
        return Math.min((int) (maxSize / scaleFactor), currentSize);
    }
}
