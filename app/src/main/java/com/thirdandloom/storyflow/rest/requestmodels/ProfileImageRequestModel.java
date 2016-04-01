package com.thirdandloom.storyflow.rest.requestmodels;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.utils.StringUtils;
import com.thirdandloom.storyflow.utils.Timber;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class ProfileImageRequestModel extends BaseRequestModel {
    private static final String IMAGE_DATA = "data:image/jpg;base64,";
    @SerializedName("image")
    private String imageData;

    public void setImageData(byte[] imageData) {
        String chunk = StringUtils.EMPTY;
        try {
            chunk = new String(Base64.encode(imageData, Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.e(e, e.getMessage());
        }
        this.imageData = IMAGE_DATA + chunk.trim();
    }

    public void setImageData(Bitmap imageData) {
        String encodedImage = encodeToBase64(imageData, Bitmap.CompressFormat.JPEG, 100);
        this.imageData = IMAGE_DATA + encodedImage;
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
