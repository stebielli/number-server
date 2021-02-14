package it.stebielli.numberserver.utils;

import java.util.concurrent.*;

public class ExecutorsUtils {
    public static final int TIMEOUT = 1000;
    public static final long KEEP_ALIVE_TIME = 0;

    public static ExecutorService newFixedThreadPool(int size) {
        return new ThreadPoolExecutor(size, size, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
    }

    public static ExecutorService newSingleThreadExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static void shutdown(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

}
