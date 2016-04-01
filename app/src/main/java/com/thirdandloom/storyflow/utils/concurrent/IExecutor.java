package com.thirdandloom.storyflow.utils.concurrent;

import android.support.annotation.Nullable;

import java.util.concurrent.Callable;

public interface IExecutor<T> {
    void submit(T task);

    /**
     * @return the result of the {@code callable} <br/>
     * or {@code null} if the executor has been shut down
     */
    @Nullable
    <Result> Result execute(Callable<Result> callable);

    void shutdown();
    boolean isShutdown();
}
