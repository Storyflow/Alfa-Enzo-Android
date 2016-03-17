package com.thirdandloom.storyflow;

import com.crashlytics.android.Crashlytics;
import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.utils.Timber;
import io.fabric.sdk.android.Fabric;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "",
        mailTo = "a.tkachenko@mobidev.biz",
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST,
        resToastText = 1)
public class StoryflowApplication extends Application {
    private static StoryflowApplication instance;

    public static StoryflowApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initTimber();
        initAcra();
        Fabric.with(this, new Crashlytics());
    }

    private void initTimber() {
        if (Config.USE_DEBUG_LOGGINING) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private void initAcra() {
        if (Config.USE_ACRA) {
            ACRA.init(this);
            ACRA.getConfig().setResToastText(R.string.crash_message);
        }
    }

    private class CrashReportingTree extends Timber.Tree {
        @Override public void i(String message, Object... args) {
            // TODO e.g., Crashlytics.log(String.format(message, args));
        }

        @Override public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override public void e(Throwable t, String message, Object... args) {
            e(message, args);
            // TODO e.g., Crashlytics.logException(t);
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {

        }
    }
}
