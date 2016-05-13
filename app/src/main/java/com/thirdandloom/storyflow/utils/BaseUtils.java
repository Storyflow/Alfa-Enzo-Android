package com.thirdandloom.storyflow.utils;

import com.thirdandloom.storyflow.StoryflowApplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public abstract class BaseUtils  {

    protected static Resources getResources() {
        return getApplicationContext().getResources();
    }

    protected static void exception(Throwable throwable) {
        Timber.e(throwable, throwable.getMessage());
    }

    protected static Context getApplicationContext() {
        return StoryflowApplication.applicationContext;
    }

    public static TelephonyManager telephonyManager() {
        return (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static NotificationManager notificationManager() {
        return (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static ConnectivityManager connectivityManager() {
        return  (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static InputMethodManager inputMethodManager() {
        return (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static ClipboardManager clipboardManager() {
        return (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public static ActivityManager activityManager() {
        return (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
    }

    public static PowerManager powerManager() {
        return (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
    }

    public static AlarmManager alarmManager() {
        return (AlarmManager) StoryflowApplication.applicationContext.getSystemService(Context.ALARM_SERVICE);
    }

    public static DownloadManager downloadManager() {
        return (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static AudioManager audioManager() {
        return (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    public static Vibrator vibrator() {
        return (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static WindowManager windowManager(Activity activity) {
        return (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
    }
}
