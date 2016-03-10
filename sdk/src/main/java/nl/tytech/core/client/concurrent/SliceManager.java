/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.client.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.core.util.SettingsManager.RunMode;
import nl.tytech.util.logger.TLogger;

/**
 * This class enables adding tasks to a separate threads.
 *
 * @author Jeroen Warmerdam & Maxim Knepfle
 */
public class SliceManager implements ParallelUpdatable {

    private static class SingletonHolder {
        private static final SliceManager INSTANCE = new SliceManager();
    }

    private class StackRunnable implements Runnable {

        protected StackTraceElement[] stack;

        @Override
        public void run() {

        }

        public void setStack(StackTraceElement[] stack) {
            this.stack = stack;
        }

    }

    public static long LONG_RUNNING_THREAD_ID = -1;
    public static long OPENGL_THREAD_ID = -1;

    private static long MAX_PARALLEL_RUNTIME = 200;

    /**
     * Execute the Runnable in the Parallel thread
     *
     * @param runnable
     */
    public static void exec(final Runnable runnable) {
        SingletonHolder.INSTANCE._exec(runnable);
    }

    /**
     * Execute the runnable on a separate long running execute service thread.
     *
     * @param callable
     */
    public static void execLongRunner(Runnable runnable) {
        SingletonHolder.INSTANCE._execLongRunner(runnable);
    }

    /**
     * Execute the runnable on the OpenGL thread.
     *
     * @param callable
     */
    public static void execOpenGL(Runnable runnable) {
        SingletonHolder.INSTANCE._execOpenGL(runnable);
    }

    /**
     * Executes the runnable.
     */
    private final ExecutorService longRunningService = Executors.newSingleThreadExecutor();

    private LinkedBlockingDeque<Runnable> fifo = new LinkedBlockingDeque<Runnable>();

    private boolean timeRunnables = SettingsManager.getRunMode() != RunMode.RELEASE;

    /**
     * Construct the SliceManager
     */
    private SliceManager() {

        UpdateManager.addParallel(this);

        longRunningService.execute(() -> {
            Thread.currentThread().setName("Client-LongRunner");
            LONG_RUNNING_THREAD_ID = Thread.currentThread().getId();
        });
    }

    private void _exec(final Runnable runnable) {

        if (Thread.currentThread().getId() == UpdateManager.PARALLELTHREAD_ID) {
            // Thread.dumpStack();
            try {
                runnable.run();
            } catch (Exception exp) {
                TLogger.exception(exp);
            }
        } else {
            // Add the parallel thread
            fifo.addLast(catchRunnableErrors(runnable, timeRunnables));
        }
    }

    private void _execLongRunner(final Runnable runnable) {

        if (Thread.currentThread().getId() == LONG_RUNNING_THREAD_ID) {
            try {
                runnable.run();
            } catch (Exception exp) {
                TLogger.exception(exp);

            }
            return;
        }

        try {
            // Add the task to the queue, so that it can be executed
            longRunningService.submit(catchRunnableErrors(runnable, false));
        } catch (Exception exp) {
            TLogger.exception(exp);
        }
    }

    private void _execOpenGL(final Runnable runnable) {

        if (EventManager.OPENGL_EVENT_EXECUTER == null) {
            TLogger.severe("No OpenGL thread defined, so cannot exucute Runnable.");
            return;
        }
        /**
         * Run on connected JME OpenGL executer
         */
        EventManager.OPENGL_EVENT_EXECUTER.enqueue(() -> {
            runnable.run();
            return null;
        });
    }

    private Runnable catchRunnableErrors(final Runnable runnable, final boolean checkTime) {

        StackRunnable runner = new StackRunnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                try {
                    runnable.run();
                } catch (Exception exp) {
                    TLogger.exception(exp);
                }
                if (checkTime) {
                    long end = System.currentTimeMillis();
                    if (end - start > MAX_PARALLEL_RUNTIME) {
                        // StringBuffer buf = new StringBuffer();
                        // for (StackTraceElement step : stack) {
                        // buf.append(step + "\n");
                        // }
                        TLogger.warning(Thread.currentThread().getName() + " Runnable takes too long: " + (end - start) + " ms.");
                    }
                }
            }
        };

        if (checkTime) {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            runner.setStack(stack);
        }
        return runner;
    }

    @Override
    public void updateParallel(float tpf) {

        // TODO: Maxim limit per frame?
        Runnable runnable = fifo.pollFirst();
        while (runnable != null) {
            runnable.run();
            runnable = fifo.pollFirst();
        }
    }
}
