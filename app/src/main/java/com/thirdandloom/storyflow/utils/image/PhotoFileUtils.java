package com.thirdandloom.storyflow.utils.image;

import com.thirdandloom.storyflow.utils.BaseUtils;
import com.thirdandloom.storyflow.utils.FileUtils;
import rx.functions.Action0;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PhotoFileUtils extends BaseUtils {
    private static final int IMAGE_QUALITY_MAX = 100;
    private static final String IMAGE = "image-";
    private static final Size IMAGE_SIZE_MAX = new Size(1080, 1920);


    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static File createOutputUniqueFile() {
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String originalName = generateImageUniqueName();
        return new File(storageDir, originalName);
    }

    public static void checkStoragePermissionsAreGuaranteed(Activity activity, Action0 onPermissionAreGuranteed) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PhotoFileUtils.PERMISSIONS_STORAGE,
                    PhotoFileUtils.REQUEST_EXTERNAL_STORAGE
            );
            return;
        }
        onPermissionAreGuranteed.call();
    }

    @NonNull
    @WorkerThread
    public static Bitmap resizeBitmap(Bitmap bitmap) {
        return new CropTakenPhotoBitmapProcessor(bitmap)
                .rotate(BitmapProcessor.INVALID_ROTATE)
                .resize(IMAGE_SIZE_MAX, false)
                .process();
    }

    @Nullable
    @WorkerThread
    public static Uri saveBitmap(@Nullable Bitmap bitmap) {
        return saveBitmap(bitmap, PhotoFileUtils.createOutputUniqueFile(), IMAGE_QUALITY_MAX);
    }

    public static void deleteFileFromCacheDir(String filePath) {
        if (!android.text.TextUtils.isEmpty(filePath)) {
            deleteFileFromCacheDir(new File(Uri.parse(filePath).getPath()));
        }
    }

    public static void deleteFileFromCacheDir(File file) {
        if ((file != null)
                && FileUtils.contains(getContext().getCacheDir(), file)) {
            file.delete();
        }
    }

    @Nullable
    private static Uri saveBitmap(@Nullable Bitmap bitmap, File outputFile, int photoQuality) {
        if (bitmap == null) {
            return null;
        }
        try {
            OutputStream out = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, photoQuality, out);
            return Uri.fromFile(outputFile);
        } catch (IOException e) {
            exception(e);
            return null;
        }
    }

    private static String generateImageUniqueName() {
        String uniqueCode = FileUtils.generateUniqueName();
        return IMAGE + uniqueCode + FileUtils.Extension.JPG;
    }
}
