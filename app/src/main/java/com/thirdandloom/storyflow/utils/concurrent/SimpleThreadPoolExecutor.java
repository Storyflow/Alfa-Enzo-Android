package com.thirdandloom.storyflow.utils.concurrent;

import com.thirdandloom.storyflow.utils.Timber;

import android.support.annotation.NonNull;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleThreadPoolExecutor extends ThreadPoolExecutor {
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()*2;

    public SimpleThreadPoolExecutor(@NonNull String name) {
        super(NUMBER_OF_CORES, NUMBER_OF_CORES, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        setThreadFactory(ThreadUtils.createThreadFactory(name));
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if ((t == null) && (r instanceof Future<?>)) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException e) {
                t = e;
            } catch (ExecutionException e) {
                t = e.getCause();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            Timber.e(t, t.getMessage());
        }
    }
}
