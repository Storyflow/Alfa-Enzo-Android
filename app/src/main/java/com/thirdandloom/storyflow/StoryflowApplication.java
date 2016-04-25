package com.thirdandloom.storyflow;

import com.crashlytics.android.Crashlytics;
import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.managers.AccountManager;
import com.thirdandloom.storyflow.preferences.CommonPreferences;
import com.thirdandloom.storyflow.rest.IRestClient;
import com.thirdandloom.storyflow.rest.RestClient;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.concurrent.SimpleExecutor;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import android.app.Application;
import android.content.res.Resources;
import android.os.Handler;

public class StoryflowApplication extends Application {
    private static StoryflowApplication instance;

    public static volatile Handler applicationHandler;

    private IRestClient restClient;
    private CommonPreferences preferences;
    private AccountManager accountManager;
    private SimpleExecutor<Runnable> backgroundThreadExecutor = new SimpleExecutor<>("backgroundThreadExecutor");

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

    public static void runBackground(Runnable runnable) {
        instance.backgroundThreadExecutor.execute(runnable);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            instance.applicationHandler.post(runnable);
        } else {
            instance.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        instance.applicationHandler.removeCallbacks(runnable);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initTimber();
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("AvenirLTStd-Roman.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        this.restClient = new RestClient(this);
        this.preferences = new CommonPreferences();
        this.accountManager = new AccountManager();

        applicationHandler = new Handler(getApplicationContext().getMainLooper());
    }

    private void initTimber() {
        if (Config.USE_DEBUG_LOGGINING) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
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
