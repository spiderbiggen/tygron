/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.client.concurrent;

import nl.tytech.util.MemoryUtils;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.logger.TLogger;

/**
 * 
 * This thread tries to update at X frames per second and can be extended to implements extra calculating threads.
 * @author Maxim Knepfle
 */
public abstract class LimitedFPSThread extends Thread {

    /**
     * Max fps
     */
    private final float maxTpf;

    private volatile boolean active = true;

    protected LimitedFPSThread(String name) {

        // /*
        // * Get framerate from settings force positive value
        // */
        // float fps = SettingsManager.getFrameRate();
        // if (fps <= 0) {
        // fps = ((Integer) SettingsType.FRAME_RATE.getDefaultValue());
        // }
        maxTpf = 1000f / 30f;

        this.setName(name);
        this.setDaemon(true);
    }

    @Override
    public final void run() {

        long start = System.currentTimeMillis();
        float executionTime = 1;
        float sleepTime = 0;
        float tpf = maxTpf;
        // extra time to make up for the delay of a slow previous frame.
        float bonusTime = 0;

        // keep looping until the app dies (thread is daemon)
        while (active) {
            tpf = System.currentTimeMillis() - start;
            start = System.currentTimeMillis();
            try {
                // call update with TPF in seconds
                this.update(tpf / 1000f);
            } catch (Exception exp) {
                TLogger.exception(exp);
            } catch (OutOfMemoryError error) {
                // TODO: Maxim: Do we want this to stop the show?
                TLogger.severe("Out of Memory! (Direct: " + MemoryUtils.getDirectMemoryMB() + " JVM: " + MemoryUtils.getJVMMemoryMB() + ")");
                System.gc();
            }

            // Limit the sleep time to max FPS and min 0.
            executionTime = System.currentTimeMillis() - start;
            sleepTime = maxTpf - executionTime + bonusTime;

            // when this frame was way too slow, add a bonus time to the next frame.
            bonusTime = sleepTime < 0 ? sleepTime : 0;

            if (sleepTime > 0) {
                ThreadUtils.sleepInterruptible((long) sleepTime);
            }
        }
    }

    public void stopThread() {
        active = false;
    }

    /**
     * Implement this method with your own calls.
     * @param tpf
     */
    public abstract void update(float tpf);
}
