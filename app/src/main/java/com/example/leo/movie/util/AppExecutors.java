package com.example.leo.movie.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class AppExecutors {
    private static final DiskIOThreadExecutor DISK_IO_THREAD_EXECUTOR = new DiskIOThreadExecutor();
    private static final Executor MAIN_THREAD_EXECUTOR = new Executor() {
        private Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mHandler.post(command);
        }
    };

    public static Executor diskIO() {
        return DISK_IO_THREAD_EXECUTOR;
    }

    public static Executor main() {
        return MAIN_THREAD_EXECUTOR;
    }
}
