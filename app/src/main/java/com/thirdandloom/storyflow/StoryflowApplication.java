package com.thirdandloom.storyflow;

import com.crashlytics.android.Crashlytics;
import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.managers.AccountManager;
import com.thirdandloom.storyflow.preferences.userDataPreferences;
import com.thirdandloom.storyflow.rest.IRestClient;
import com.thirdandloom.storyflow.rest.RestClient;
import com.thirdandloom.storyflow.service.UploadStoriesService;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.concurrent.SimpleExecutor;
import com.thirdandloom.storyflow.utils.connectivity.ConnectivityObserver;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import android.app.Application;
import android.content.res.Resources;
import android.os.Handler;

public class StoryflowApplication extends Application {
    private static StoryflowApplication instance;
    public static volatile Handler applicationHandler;

    private IRestClient restClient;
    private userDataPreferences userDataPreferences;
    private AccountManager accountManager;
    private ConnectivityObserver connectivityObserver;
    private SimpleExecutor<Runnable> backgroundThreadExecutor = new SimpleExecutor<>("backgroundThreadExecutor");

    public static StoryflowApplication getInstance() {
        return instance;
    }

    public static userDataPreferences userDataPreferences() {
        return instance.userDataPreferences;
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

    public static ConnectivityObserver connectivityObserver() {
        return instance.connectivityObserver;
    }

    public static void runBackground(Runnable runnable) {
        instance.backgroundThreadExecutor.execute(runnable);
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
        applicationHandler = new Handler(getApplicationContext().getMainLooper());

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
        this.userDataPreferences = new userDataPreferences();
        this.accountManager = new AccountManager();
        this.connectivityObserver = new ConnectivityObserver();
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
