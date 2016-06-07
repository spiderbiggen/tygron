/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;
import nl.tytech.util.logger.TLogger;

/**
 * This Util class for system memory detection
 *
 * @author Maxim Knepfle
 */
public class MemoryUtils {

    private static long maxJVMMem = 0;

    private static long maxDirectMem = 0;

    private final static long mb = 1024 * 1024;

    private static Class<?> nioBits;

    private static Field reservedMemory;

    static {
        try {
            nioBits = Class.forName("java.nio.Bits");
            reservedMemory = nioBits.getDeclaredField("reservedMemory");
            reservedMemory.setAccessible(true);
        } catch (Exception e) {
            TLogger.exception(e);
        }
    }

    public final static int MAX_JVM_MEMORY_SETTING_MB;
    private final static String JVM_MEMORY_SETTING = "-Xmx";

    static {
        int maxJVM = -1;
        try {
            RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
            for (String arg : bean.getInputArguments()) {
                if (arg.startsWith(JVM_MEMORY_SETTING) && arg.endsWith("M")) {
                    maxJVM = Integer.valueOf(arg.replaceFirst(JVM_MEMORY_SETTING, "").replaceFirst("M", ""));
                } else if (arg.startsWith(JVM_MEMORY_SETTING) && arg.endsWith("G")) {
                    maxJVM = Integer.valueOf(arg.replaceFirst(JVM_MEMORY_SETTING, "").replaceFirst("G", "")) * 1024;
                }
            }
        } catch (Exception e) {
            TLogger.exception(e);
        }
        if (maxJVM < 0) {
            TLogger.severe("Unable to find JVM Memory setting!");
        }
        MAX_JVM_MEMORY_SETTING_MB = maxJVM;
    }

    public static void dumpMemoryUsage() {
        try {

            long directMem = getDirectMemoryMB();
            long jvmMem = getJVMMemoryMB();
            // long allocatedMem = Runtime.getRuntime().totalMemory();
            long maxMemNow = jvmMem + directMem;
            if (jvmMem > maxJVMMem) {
                maxJVMMem = jvmMem;
            }
            if (directMem > maxDirectMem) {
                maxDirectMem = directMem;
            }
            System.out.println("\nActual Direct: " + directMem + " MB");
            System.out.println("Actual JVM: " + jvmMem + " MB");
            System.out.println("Actual Total: " + maxMemNow + " MB");

            System.out.println("Max measured JVM: " + maxJVMMem + " MB");
            System.out.println("Max measured Direct: " + maxDirectMem + " MB");
            System.out.println("Max measured Total: " + (maxJVMMem + maxDirectMem) + " MB");
        } catch (Exception e) {
            TLogger.exception(e);
        }
    }

    public static long getDirectMemoryMB() {
        try {
            synchronized (nioBits) {
                // Note: Changed from Long to AtomicLong in Java 8 Update 74
                Object bitsValue = reservedMemory.get(null);
                long value = bitsValue instanceof AtomicLong ? ((AtomicLong) bitsValue).get() : (Long) bitsValue;
                return value / mb;
            }
        } catch (Exception e) {
            TLogger.exception(e);
            return -1;
        }
    }

    public static long getFreeJVMMemoryMB() {
        return Runtime.getRuntime().freeMemory() / mb;
    }

    public static long getJVMMemoryMB() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb;
    }
}
