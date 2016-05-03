/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.data.core.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.util.StringUtils;

/**
 * Moment
 * <p>
 * This class keeps track of sim time moment.
 * <p>
 *
 *
 * @author Maxim Knepfle
 */
public class Moment extends Item {

    public enum SimTime {
        /**
         * Planning games run only in the maquette map, with no simulation time.
         */
        PLANNING(),
        /**
         * Timeline games run over a period of simulated time (e.g. 30 years).
         */
        TIMELINE;
    }

    /**
     * Day in milliseconds.
     */
    public final static long DAY = 24L * 60L * 60L * 1000L;

    /**
     * Hour in milliseconds.
     */
    public final static long HOUR = 60L * 60L * 1000L;

    /**
     * Minute in milliseconds.
     */
    public final static long MINUTE = 60L * 1000L;

    /**
     * Week in milliseconds.
     */
    public final static long WEEK = 7L * 24L * 60L * 60L * 1000L;

    /**
     * Year in milliseconds.
     */
    public final static long YEAR = 365L * 24L * 60L * 60L * 1000L;

    /**
     * Month in milliseconds (avg approximation)
     */
    public final static long MONTH_AVG = YEAR / 12l;

    /**
     * Current sim time.
     */
    public static final Integer CURRENT_POSTION = 0;

    /**
     * First time the sim time is automatically paused.
     */
    public static final Integer FIRST_STOP_POSTION = 2;

    /**
     * The game is started with this sim time moment.
     */
    public static final Integer SIMULATION_START_POSTION = 1;

    private static final long serialVersionUID = 5776008764383172107L;

    private final static int EPOCH = 1970;

    public static Moment getEndingMoment(Collection<Moment> values) {

        Iterator<Moment> iterator = values.iterator();
        Moment lastMoment = iterator.next();
        while (iterator.hasNext()) {
            lastMoment = iterator.next();
        }
        return lastMoment;
    }

    public final static double getProgress(long current, Collection<Moment> pauses) {

        ArrayList<Moment> list = new ArrayList<Moment>(pauses);
        Moment begin = null;
        Moment end = null;
        Moment tempBegin, tempEnd;
        for (int i = Moment.SIMULATION_START_POSTION; i < list.size() - 1; ++i) {
            tempBegin = list.get(i);
            tempEnd = list.get(i + 1);
            if (current > tempBegin.getMillis() && current < tempEnd.getMillis()) {
                begin = tempBegin;
                end = tempEnd;
                break;
            }
        }

        if (begin == null) {
            return 0.0;
        }
        if (end == null) {
            return 1d;
        }
        return (current - begin.getMillis()) / (end.getMillis() - begin.getMillis());

    }

    public static int getYear(long now) {
        return EPOCH + (int) (now / Moment.YEAR);
    }

    // time in millis
    @DoNotSaveToInit("0")
    @XMLValue
    private long moment = 0;

    public Moment() {
    }

    public Moment(long timeMillis) {
        this.setMillis(timeMillis);
    }

    /**
     * @return the moment time as UTC milliseconds from the epoch.
     */
    public long getMillis() {
        return moment;
    }

    public void setMillis(long timeMillis) {
        this.moment = timeMillis;
    }

    @Override
    public String toString() {

        SimTimeSetting setting = this.getItem(MapLink.SIMTIME_SETTINGS, SimTimeSetting.Type.TYPE);
        SimTime simTime = setting.getEnumValue(SimTime.class);
        return StringUtils.dateToHumanString(this.getMillis(), simTime == SimTime.TIMELINE);
    }
}
