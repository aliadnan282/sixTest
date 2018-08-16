package com.workout.sixpacksabs.helper;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by AdnanAli on 12/19/2017.
 */

public final class SixPackThreadPoolExecutor extends ThreadPoolExecutor {

    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    //private TelloRejectedExecutionHandler rejectedExecutionHandler;
//    private ThreadFactory threadFactory;
//    private ThreadPoolExecutor threadPoolExecutor;
    private static SixPackThreadPoolExecutor instance;

    private SixPackThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public static SixPackThreadPoolExecutor getInstance() {
        if (instance == null) {
            instance = new SixPackThreadPoolExecutor(NUMBER_OF_CORES * 2, NUMBER_OF_CORES * 2, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }
        return instance;
    }

    @Override
    public void execute(Runnable command) {
        Log.d("SixPackThreadPoolExecut", String.format("TREADPOOL [monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                this.getPoolSize(),
                this.getCorePoolSize(),
                this.getActiveCount(),
                this.getCompletedTaskCount(),
                this.getTaskCount(),
                this.isShutdown(),
                this.isTerminated()));
        super.execute(command);
    }
//    private TelloThreadPoolExecutor(){
//        super();
//        rejectedExecutionHandler = new TelloRejectedExecutionHandler();
//        threadFactory = Executors.defaultThreadFactory();
//        threadPoolExecutor = new ThreadPoolExecutor(2,10,10, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(1024),threadFactory,rejectedExecutionHandler);
//    }


    public void shutDownWorkerPool() {
        this.shutdown();
    }
}
