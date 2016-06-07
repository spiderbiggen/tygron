/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.client.concurrent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;

/**
 * You can add random objects to this class that need to updated and do not belong in the OpenGL thread. Update freq is 60FPS by default.
 *
 * Note objects are weak referenced, thus removed when no one links!
 *
 * @author Maxim Knepfle
 */
public class UpdateManager {

    /**
     * Internal class that counts the frame rate for each thread. (Not very clean code).
     * @author Maxim
     *
     */
    private class FPSCounter implements OpenGLUpdatable, ParallelUpdatable, Updatable {

        private volatile int fps = 0;
        private int frameCounter = 0;
        private long time = System.currentTimeMillis();

        public int getFps() {
            return fps;
        }

        @Override
        public void update(float tpf) {
            updateInner();
        }

        private void updateInner() {
            if (System.currentTimeMillis() - time > 1000) {
                fps = frameCounter;
                frameCounter = 0;
                time = System.currentTimeMillis();
            }
            frameCounter++;
        }

        @Override
        public void updateOpenGL(float tpf) {
            updateInner();
        }

        @Override
        public void updateParallel(float tpf) {
            updateInner();
        }
    }

    private class FXCounter extends AnimationTimer {

        private FPSCounter inner = new FPSCounter();

        public int getFps() {
            return inner.getFps();
        }

        @Override
        public void handle(long now) {
            inner.updateInner();
        }
    }

    private class ParallelUpdater extends LimitedFPSThread {

        private ParallelUpdater() {
            super("Client-" + ParallelUpdater.class.getSimpleName());
        }

        @Override
        public void update(float tpf) {
            updateParallel(tpf);
        }
    };

    private static class SingletonHolder {
        private static final UpdateManager INSTANCE = new UpdateManager();
    }

    public static long PARALLELTHREAD_ID = -1;

    public static void addOpenGL(OpenGLUpdatable updatable) {
        SingletonHolder.INSTANCE._addOpenGL(updatable);
    }

    public static void addParallel(ParallelUpdatable updatable) {
        SingletonHolder.INSTANCE._addParallel(updatable);
    }

    public static int getJavaFXFPS() {
        return SingletonHolder.INSTANCE.fxCounter.getFps();
    }

    public static int getOpenGLFPS() {
        return SingletonHolder.INSTANCE.openGLCounter.getFps();
    }

    public static int getParallelFPS() {
        return SingletonHolder.INSTANCE.parallelCounter.getFps();
    }

    public static void removeOpenGL(Object updatable) {
        SingletonHolder.INSTANCE._removeOpenGL(updatable);
    }

    public static void removeParallel(Object updatable) {
        SingletonHolder.INSTANCE._removeParallel(updatable);
    }

    /**
     * Do not call this method except from main OpenGL loop update method.
     * @param tpf
     */
    public static void updateOpenGL(float tpf) {
        SingletonHolder.INSTANCE._updateOpenGL(tpf);
    }

    private List<WeakReference<ParallelUpdatable>> parallelUpdatables = new ArrayList<WeakReference<ParallelUpdatable>>();

    private List<WeakReference<OpenGLUpdatable>> openGLUpdatables = new ArrayList<WeakReference<OpenGLUpdatable>>();

    private List<WeakReference<ParallelUpdatable>> tweakParallelUpdatables = new ArrayList<WeakReference<ParallelUpdatable>>();

    private List<WeakReference<OpenGLUpdatable>> tweakOpenGLUpdatables = new ArrayList<WeakReference<OpenGLUpdatable>>();

    private ParallelUpdater parallelUpdater;

    private FPSCounter openGLCounter, parallelCounter;

    private FXCounter fxCounter;

    private UpdateManager() {

        parallelUpdater = new ParallelUpdater();
        parallelUpdater.start();
        PARALLELTHREAD_ID = parallelUpdater.getId();

        openGLCounter = new FPSCounter();
        this._addOpenGL(openGLCounter);
        parallelCounter = new FPSCounter();
        this._addParallel(parallelCounter);

        fxCounter = new FXCounter();
        fxCounter.start();
    }

    private void _addOpenGL(OpenGLUpdatable updatable) {
        synchronized (tweakOpenGLUpdatables) {
            tweakOpenGLUpdatables.add(new WeakReference<OpenGLUpdatable>(updatable));
            openGLUpdatables = new ArrayList<WeakReference<OpenGLUpdatable>>(tweakOpenGLUpdatables);
        }
    }

    private void _addParallel(ParallelUpdatable updatable) {
        synchronized (tweakParallelUpdatables) {
            tweakParallelUpdatables.add(new WeakReference<ParallelUpdatable>(updatable));
            parallelUpdatables = new ArrayList<WeakReference<ParallelUpdatable>>(tweakParallelUpdatables);
        }
    }

    private void _removeOpenGL(Object updatable) {
        synchronized (tweakOpenGLUpdatables) {
            for (WeakReference<OpenGLUpdatable> updatableReference : tweakOpenGLUpdatables) {
                // check for both objects!
                if (updatableReference.get() == updatable || updatableReference == updatable) {
                    tweakOpenGLUpdatables.remove(updatableReference);
                    break;
                }
            }
            openGLUpdatables = new ArrayList<WeakReference<OpenGLUpdatable>>(tweakOpenGLUpdatables);
        }
    }

    private void _removeParallel(Object updatable) {
        synchronized (tweakParallelUpdatables) {
            for (WeakReference<ParallelUpdatable> updatableReference : tweakParallelUpdatables) {
                // check for both objects!
                if (updatableReference.get() == updatable || updatableReference == updatable) {
                    tweakParallelUpdatables.remove(updatableReference);
                    break;
                }
            }
            parallelUpdatables = new ArrayList<WeakReference<ParallelUpdatable>>(tweakParallelUpdatables);
        }
    }

    private void _updateOpenGL(float tpf) {
        for (int i = 0; i < openGLUpdatables.size(); i++) {
            WeakReference<OpenGLUpdatable> updatableReference = openGLUpdatables.get(i);
            OpenGLUpdatable updatable = updatableReference.get();
            if (updatable != null) {
                updatable.updateOpenGL(tpf);
            } else {
                // remove and break, no problem skipping one frame
                _removeOpenGL(updatable);
                break;
            }
        }
    }

    private void updateParallel(float tpf) {
        for (int i = 0; i < parallelUpdatables.size(); i++) {
            WeakReference<ParallelUpdatable> updatableReference = parallelUpdatables.get(i);
            ParallelUpdatable updatable = updatableReference.get();
            if (updatable != null) {
                updatable.updateParallel(tpf);
            } else {
                // remove and break, no problem skipping one frame
                _removeParallel(updatable);
                break;
            }
        }
    }
}
