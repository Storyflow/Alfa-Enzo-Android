package com.thirdandloom.storyflow.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.UUID;

public class FileUtils extends BaseUtils {

    public interface Extension {
        String JPG = ".jpg";
    }

    @NonNull
    public static String generateUniqueName() {
        return String.valueOf(UUID.randomUUID().hashCode());
    }

    public static boolean contains(File dir, File file) {
        File parent = file.getParentFile();
        while (parent != null) {
            if (dir.equals(parent)) {
                return true;
            }
            parent = parent.getParentFile();
        }
        return false;
    }
}
