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
 * An exception that indicates a StopWatch was not used properly.
 * 
 * @author Michael Franken
 * @version $Revision: 1.8 $
 */
public class StopWatchException extends Exception {
    /**
     * Comment for <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 3544669564400121145L;

    /**
     * Constructs a <code>StopWatchException</code> with the specified message and no root cause.
     * 
     * @param msg the detail message.
     */
    public StopWatchException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>StopWatchException</code> with the specified message and root cause.
     * 
     * @param msg the detail message.
     * @param t the root cause
     */
    public StopWatchException(final String msg, final Throwable t) {
        super(msg, t);
    }
}
