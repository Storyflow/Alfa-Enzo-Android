package com.thirdandloom.storyflow.utils.image;

import android.graphics.Bitmap;
import android.util.Base64;

import com.thirdandloom.storyflow.utils.BaseUtils;

import java.io.ByteArrayOutputStream;

public class EncodeUtils extends BaseUtils {
    private static final String IMAGE_DATA = "data:image/jpg;base64,";

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return IMAGE_DATA + Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
