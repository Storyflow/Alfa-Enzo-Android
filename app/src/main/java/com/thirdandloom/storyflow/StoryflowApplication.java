package com.thirdandloom.storyflow;

import com.crashlytics.android.Crashlytics;
import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.managers.AccountManager;
import com.thirdandloom.storyflow.managers.PendingStoriesManager;
import com.thirdandloom.storyflow.preferences.ApplicationPreferences;
import com.thirdandloom.storyflow.preferences.UserPreferences;
import com.thirdandloom.storyflow.rest.IRestClient;
import com.thirdandloom.storyflow.rest.RestClient;
import com.thirdandloom.storyflow.service.UploadStoriesService;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.concurrent.SimpleExecutor;
import io.fabric.sdk.android.Fabric;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import java.util.concurrent.Future;

public class StoryflowApplication extends Application {
    private static volatile Handler applicationHandler;
    private static volatile SimpleExecutor<Runnable> backgroundThreadExecutor;

    public static volatile UserPreferences userPreferences;
    public static volatile ApplicationPreferences applicationPreferences;
    public static volatile Context applicationContext;

    private static StoryflowApplication instance;
    private IRestClient restClient;
    private AccountManager accountManager;
    private PendingStoriesManager pendingStoriesManager;

    public static AccountManager account() {
        return instance.accountManager;
    }

    public static IRestClient restClient() {
        return instance.restClient;
    }

    public static Resources resources() {
        return applicationContext.getResources();
    }

    public static PendingStoriesManager getPendingStoriesManager() {
        return instance.pendingStoriesManager;
    }

    public static void runBackground(Runnable runnable) {
        runBackground(runnable, null);
    }

    public static void runBackground(Runnable runnable, Action1<Future<?>> computation) {
        backgroundThreadExecutor.execute(runnable, computation);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            applicationHandler.post(runnable);
        } else {
            applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        applicationHandler.removeCallbacks(runnable);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationContext = getApplicationContext();
        applicationHandler = new Handler(applicationContext.getMainLooper());
        backgroundThreadExecutor = new SimpleExecutor<>("backgroundThreadExecutor");
        userPreferences = new UserPreferences();
        applicationPreferences = new ApplicationPreferences();

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
        this.accountManager = new AccountManager();
        this.pendingStoriesManager = new PendingStoriesManager();
        UploadStoriesService.notifyService();
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
