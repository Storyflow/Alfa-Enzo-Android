package com.thirdandloom.storyflow.utils.concurrent;

import com.google.common.util.concurrent.MoreExecutors;
import com.thirdandloom.storyflow.utils.Timber;
import rx.functions.Action1;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class SimpleExecutor<Task extends Runnable> implements IExecutor<Task> {
    private static final int TERMINATION_TIMEOUT = 30_000; // milliseconds

    private final ExecutorService asyncExecutor;
    private final ExecutorService syncExecutor = MoreExecutors.newDirectExecutorService();

    public SimpleExecutor(@NonNull String name) {
        this(new SimpleThreadPoolExecutor(name));
    }

    public SimpleExecutor(ExecutorService asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public void submit(Task task) {
        asyncSubmit(task);
    }

    @Nullable
    @Override
    public <Result> Result execute(Callable<Result> callable) {
        if (syncExecutor.isShutdown()) {
            return null;
        }
        try {
            Future<Result> future = syncExecutor.submit(callable); // throws RejectedExecutionException if the executor has been already shut down
            return future.get();
        } catch (InterruptedException | ExecutionException | RejectedExecutionException e) {
            Timber.e(e, e.getMessage());
            return null;
        }
    }

    public <T> void submit(AsyncTask<T, ?, ?> asyncTask, T ... params) {
        if (isShutdown()) {
            return;
        }
        asyncTask.executeOnExecutor(asyncExecutor, params);
    }

    @Override
    public void shutdown() {
        syncExecutor.shutdownNow();
        asyncExecutor.shutdownNow();
        try {
            waitExecutorTermination(syncExecutor);
            waitExecutorTermination(asyncExecutor);
        } catch (InterruptedException e) {
            Timber.e(e, e.getMessage());
        }
    }

    @Override
    public boolean isShutdown() {
        return syncExecutor.isShutdown() || asyncExecutor.isShutdown();
    }


    @Override
    public void execute(@NonNull Runnable command, Action1<Future<?>> computation) {
        Future<?> submitFuture = asyncSubmit(command);
        if (computation != null && submitFuture != null) {
            computation.call(submitFuture);
        }
    }

    @Nullable
    private Future<?> asyncSubmit(Runnable task) {
        if (!asyncExecutor.isShutdown()) {
            return asyncExecutor.submit(task);
        } else {
            return null;
        }
    }

    private static void waitExecutorTermination(ExecutorService executor) throws InterruptedException {
        //noinspection StatementWithEmptyBody
        while (!executor.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS)) {
        }
    }
}
