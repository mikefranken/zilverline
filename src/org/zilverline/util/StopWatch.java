/*
 * Copyright 2003-2004 Michael Franken, Zilverline.
 *
 * The contents of this file, or the files included with this file, are subject to
 * the current version of ZILVERLINE Collaborative Source License for the
 * Zilverline Search Engine (the "License"); You may not use this file except in
 * compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.zilverline.org.
 *
 * See the License for the rights, obligations and
 * limitations governing use of the contents of the file.
 *
 * The Original and Upgraded Code is the Zilverline Search Engine. The developer of
 * the Original and Upgraded Code is Michael Franken. Michael Franken owns the
 * copyrights in the portions it created. All Rights Reserved.
 *
 */

package org.zilverline.util;

/**
 * StopWatch reports elapsed time between start and stop.
 * 
 * @author Michael Franken
 * @version $Revision: 1.12 $
 */
public class StopWatch {
    /** Time the Stopwatch started. */
    private long start = 0;

    /** Time the Stopwatch stopped. */
    private long stop = 0;

    /**
     * Start ticking, resets the watch.
     */
    public final void start() {
        start = System.currentTimeMillis();
    }

    /**
     * Stop ticking.
     */
    public final void stop() {
        stop = System.currentTimeMillis();
    }

    /**
     * Calculates time elapsed. If stop has not been called yet, the current time is taken to calculate elapsed time.
     * 
     * @return the time elapsed between start and stop as String. The string contains two of days, hours, minutes, seconds and
     *         milliseconds.
     * 
     * @throws StopWatchException If StopWatch was never started.
     */
    public final String elapsedTime() throws StopWatchException {
        long difference;

        if (stop == 0) {
            long now = System.currentTimeMillis();

            difference = (now - start); // in millis
        } else {
            difference = (stop - start); // in millis
        }

        if (start == 0) {
            throw new StopWatchException("StopWatch never started");
        }

        long mils = difference % 1000;

        difference = (difference - mils) / 1000; // in seconds

        long secs = difference % 60;

        difference = (difference - secs) / 60; // in minutes

        long minutes = difference % 60;

        difference = (difference - minutes) / 60; // in hours

        long hours = difference % 24;

        difference = (difference - hours) / 24; // in days

        long days = difference;
        String message = "";

        if (days > 0) {
            message = days + " days and  " + hours + " hours";
        } else if (hours > 0) {
            message = hours + " hours and " + minutes + " minutes";
        } else if (minutes > 0) {
            message = minutes + " minutes and " + secs + " seconds";
        } else if (secs > 0) {
            message = secs + " seconds and " + mils + " milliseconds";
        } else {
            message = mils + " milliseconds";
        }

        return message;
    }
}
