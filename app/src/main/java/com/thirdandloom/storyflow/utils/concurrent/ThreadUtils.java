package com.thirdandloom.storyflow.utils.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {
    public static ThreadFactory createThreadFactory(@NonNull String name) {
        return new ThreadFactoryBuilder()
                .setNameFormat(name + " #%d")
                .build();
    }

    public static ExecutorService newFixedThreadPool(int threadsAmount, @NonNull String name) {
        ThreadFactory threadFactory = createThreadFactory(name);
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        return new ThreadPoolExecutor(threadsAmount, threadsAmount, 0L, TimeUnit.MILLISECONDS, workQueue, threadFactory);
    }
}
