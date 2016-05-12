package com.thirdandloom.storyflow.utils.concurrent;

import rx.functions.Action1;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface IExecutor<T> {
    void submit(T task);

    /**
     * @return the result of the {@code callable} <br/>
     * or {@code null} if the executor has been shut down
     */
    @Nullable
    <Result> Result execute(Callable<Result> callable);

    void execute(@NonNull Runnable command, Action1<Future<?>> future);

    void shutdown();
    boolean isShutdown();
}
