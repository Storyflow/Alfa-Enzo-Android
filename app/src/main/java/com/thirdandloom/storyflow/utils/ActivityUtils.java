package com.thirdandloom.storyflow.utils;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.utils.image.PhotoFileUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class ActivityUtils extends BaseUtils {

    public static String capturePhoto(Activity activity, int requestCode) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = PhotoFileUtils.createOutputUniqueFile();
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photoFile));
        activity.startActivityForResult(captureIntent, requestCode);
        return photoFile.getAbsolutePath();
    }

    public static void selectPhoto(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), requestCode);
    }
}
