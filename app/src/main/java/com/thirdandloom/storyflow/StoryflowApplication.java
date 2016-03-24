package com.thirdandloom.storyflow;

import com.crashlytics.android.Crashlytics;
import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.managers.AccountManager;
import com.thirdandloom.storyflow.preferences.CommonPreferences;
import com.thirdandloom.storyflow.rest.IRestClient;
import com.thirdandloom.storyflow.rest.RestClient;
import com.thirdandloom.storyflow.utils.Timber;
import io.fabric.sdk.android.Fabric;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.res.Resources;

@ReportsCrashes(formKey = "",
        mailTo = "a.tkachenko@mobidev.biz",
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST,
        resToastText = 1)
public class StoryflowApplication extends Application {
    private static StoryflowApplication instance;

    private IRestClient restClient;
    private CommonPreferences preferences;
    private AccountManager accountManager;

    public static StoryflowApplication getInstance() {
        return instance;
    }

    public static CommonPreferences preferences() {
        return instance.preferences;
    }

    public static AccountManager account() {
        return instance.accountManager;
    }

    public static IRestClient restClient() {
        return instance.restClient;
    }

    public static Resources resources() {
        return instance.getResources();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initTimber();
        initAcra();
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
        this.restClient = new RestClient();
        this.preferences = new CommonPreferences();
        this.accountManager = new AccountManager();
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
            Crashlytics.log(String.format(message, args));
        }

        @Override public void i(Throwable t, String message, Object... args) {
            i(message, args);
        }

        @Override public void e(String message, Object... args) {
            i("ERROR: " + message, args);
        }

        @Override public void e(Throwable t, String message, Object... args) {
            e(message, args);
            Crashlytics.logException(t);
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {

        }
    }
}
