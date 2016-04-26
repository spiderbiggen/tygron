/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import nl.tytech.core.net.Lord;
import nl.tytech.core.net.Network;
import nl.tytech.core.util.PowerShare.MultiTaskList.ListTask;
import nl.tytech.util.MathUtils;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.concurrent.ThreadPriorities;
import nl.tytech.util.logger.TLogger;

/**
 * Share calculation power between threads using one Thread Pool
 * @author Maxim Knepfle
 *
 */
public class PowerShare {

    public enum Bound {

        /**
         * the runnable is ONLY limited by the amount of available CPU power
         */
        MAX_CPU,

        /**
         * the runnable is limited by the amount of e.g. available Internet speed or thread sleeps a lot (executes slower and more
         * parallelism then CPU share)
         */
        MAX_PARRALISM
    }

    /**
     * Helper object to simplify common multi threading operations
     */
    public static class MultiTask {

        private AtomicInteger unfinishedTasks = new AtomicInteger(0);

        protected AtomicInteger totalTasks = new AtomicInteger(0);

        private long start = System.currentTimeMillis();

        private final Lord lord;

        private final ForkJoinPool pool;

        public MultiTask(Lord lord, Bound bound) {
            this.lord = lord;
            this.pool = bound == Bound.MAX_CPU ? maxCpuService : maxParallelService;
        }

        public void execute(Runnable runnable) {

            unfinishedTasks.incrementAndGet();
            totalTasks.incrementAndGet();

            pool.execute(() -> {
                try {
                    runnable.run();
                } catch (Exception e) {
                    throw e;
                } finally {
                    unfinishedTasks.decrementAndGet();
                }
            });
        }

        public long getExecutionTimeMS() {
            return System.currentTimeMillis() - start;
        }

        public int getTaskAmount() {
            return totalTasks.get();
        }

        public int getUnfinishedTaskAmount() {
            return unfinishedTasks.get();
        }

        public boolean waitUntilAllFinished() {

            while (unfinishedTasks.get() > 0) {

                // sleep on it depending on counter how long
                ThreadUtils.sleepInterruptible(MathUtils.clamp(unfinishedTasks.get() * 4, 0, 40));

                // check for shutdowns when Lord is present.
                if (lord != null && lord.isShutdown()) {
                    TLogger.warning("Lord shutdown, cancel MultiTask.");
                    return false;
                }
            }
            return true;
        }
    }

    public static class MultiTaskList<R> extends MultiTask {

        public interface ListTask<T, R> {
            public List<R> run(List<T> list);
        }

        private List<R> result = new ArrayList<>();

        public MultiTaskList(Lord lord) {
            super(lord, Bound.MAX_PARRALISM);
        }

        public List<R> getResult() {
            return result;
        }
    }

    private static class Worker extends ForkJoinWorkerThread {
        private Worker(ForkJoinPool pool, String name) {
            super(pool);
            this.setName(name + "-" + counter.getAndIncrement() + "-" + pool.getActiveThreadCount());
            this.setPriority(ThreadPriorities.LOW);
        }
    }

    public final static int MAX_THREADS = 512;
    public final static int MIN_THREADS = 4;
    private final static AtomicInteger counter = new AtomicInteger(1);
    public final static int CPU_THREADS;

    private static final ForkJoinPool maxCpuService;
    private static final ForkJoinPool maxParallelService;

    static {
        Network.AppType appType = SettingsManager.getApplicationType();
        int cores = Runtime.getRuntime().availableProcessors();
        CPU_THREADS = MathUtils.clamp(cores * 2, MIN_THREADS, MAX_THREADS);
        maxCpuService = new ForkJoinPool(CPU_THREADS, pool -> new Worker(pool, "CPUShare"), null, true);
        maxParallelService = new ForkJoinPool(MAX_THREADS, pool -> new Worker(pool, "ParallelShare"), null, true);
        TLogger.info("Setup PowerShare: " + appType + " with: " + cores + " cores to use: " + CPU_THREADS + " CPU threads.");
    }

    public static void execute(Bound bound, int priority, Runnable runnable) {

        ForkJoinPool pool = bound == Bound.MAX_CPU ? maxCpuService : maxParallelService;
        pool.execute(() -> {
            if (Thread.currentThread().getPriority() != priority) {
                Thread.currentThread().setPriority(priority);
            }
            runnable.run();
        });
    }

    public static void execute(Bound bound, Runnable runnable) {
        execute(bound, ThreadPriorities.LOW, runnable);
    }

    public static void execute(Runnable runnable) {
        execute(Bound.MAX_PARRALISM, runnable);
    }

    public static long getActiveCPUThreadCount() {
        return maxCpuService.getActiveThreadCount();
    }

    public static long getActiveParallelThreadCount() {
        return maxParallelService.getActiveThreadCount();
    }

    public static long getCPUPoolSize() {
        return maxCpuService.getPoolSize();
    }

    public static long getParallelPoolSize() {
        return maxParallelService.getPoolSize();
    }

    public static MultiTask multiTask(Bound bound) {
        return multiTask(null, bound);
    }

    public static MultiTask multiTask(Lord lord, Bound bound) {
        return new MultiTask(lord, bound);
    }

    public static <T, R> MultiTaskList<R> multiTaskListAndWait(List<T> completeList, int parallelisation, ListTask<T, R> run) {

        // create list task
        MultiTaskList<R> mt = new MultiTaskList<>(null);
        if (completeList == null) {
            return mt;
        }
        List<R> resultList = mt.getResult();
        // make sure always at least 1 task
        int listTaskSize = Math.max(1, (int) Math.ceil(completeList.size() / (double) parallelisation));

        // small lists skip, no MULTI usefully
        if (completeList.size() <= listTaskSize) {

            List<R> result = run.run(completeList);
            if (result != null) {
                resultList.addAll(result);
            }
            // count as 1 task done
            if (!completeList.isEmpty()) {
                mt.totalTasks.set(1);
            }
            return mt;
        }

        for (int i = 0; i < completeList.size(); i += listTaskSize) {
            int startIndex = Math.min(completeList.size() - 1, i);
            int endIndex = Math.min(completeList.size(), i + listTaskSize);
            List<T> subList = completeList.subList(startIndex, endIndex);
            if (subList.isEmpty()) {
                continue;
            }
            mt.execute(() -> {
                List<R> result = run.run(subList);
                if (result != null) {
                    synchronized (resultList) {
                        resultList.addAll(result);
                    }
                }
            });
        }
        mt.waitUntilAllFinished();
        return mt;
    }

    public static void shutdown() {

        int counter = 0;
        for (ForkJoinPool pool : new ForkJoinPool[] { maxParallelService, maxCpuService }) {
            try {
                counter++;
                TLogger.info("Shutdown Pool: " + counter + "... (Please wait up to one minute for Pool: " + counter + " to terminate)");
                long start = System.currentTimeMillis();
                pool.shutdown();
                boolean terminated = pool.awaitTermination(1, TimeUnit.MINUTES);
                if (terminated) {
                    TLogger.info("Pool: " + counter + " terminated gracefully in: " + (System.currentTimeMillis() - start) + " ms.");
                } else {
                    TLogger.warning("Pool: " + counter + " was FORCED to shutdown: " + pool.getActiveThreadCount()
                            + " threads remain active!");
                }
            } catch (Exception e) {
                TLogger.exception(e);
            }
        }
    }
}
