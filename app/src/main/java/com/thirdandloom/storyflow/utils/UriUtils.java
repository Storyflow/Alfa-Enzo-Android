package com.thirdandloom.storyflow.utils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.Locale;

public class UriUtils extends BaseUtils {
    public static Uri fromFile(String path) {
        return Uri.fromFile(new File(path));
    }

    public static Uri fromFile(Uri uri) {
        return Uri.fromFile(new File(uri.getPath()));
    }

    public static File newFile(String uri) {
        return new File(Uri.parse(uri).getPath());
    }

    public static String getPath(String uriString) {
        if (uriString == null) {
            return null;
        }
        return getPathInternal(uriString);
    }

    @Nullable
    public static String toString(@Nullable Uri uri) {
        return (uri != null) ? uri.toString() : null;
    }

    /**
     * @see Uri#normalizeScheme()
     */
    @NonNull
    public static Uri normalizeScheme(@NonNull Uri uri) {
        String scheme = uri.getScheme();
        if (scheme == null) {
            return uri;
        }
        String lowerScheme = scheme.toLowerCase(Locale.ROOT);
        if (scheme.equals(lowerScheme)) {
            return uri;
        }

        return uri.buildUpon()
                .scheme(lowerScheme)
                .build();
    }

    private static String getPathInternal(String uriString) {
        return Uri.parse(uriString).getPath();
    }
}
