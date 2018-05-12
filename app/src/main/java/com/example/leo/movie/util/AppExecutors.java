package com.example.leo.movie.util;

import java.util.concurrent.Executor;

public class AppExecutors {
    private static final DiskIOThreadExecutor DISK_IO_THREAD_EXECUTOR = new DiskIOThreadExecutor();

    public static Executor diskIO() {
        return DISK_IO_THREAD_EXECUTOR;
    }
}
