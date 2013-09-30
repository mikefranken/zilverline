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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class (a Thread) for getting Stream output from running process. Used in Runtime.exec(). Copied from
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 * 
 * @author Michael Daconta
 * @version $Revision: 1.11 $
 */
public class StreamGobbler extends Thread {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(StreamGobbler.class);

    /** The Stream to get the output from. */
    private InputStream is;

    /** The type of process output (used for logging, e.g. STDIN, STDERR). */
    private String type;

    /**
     * Creates a new StreamGobbler object.
     * 
     * @param thisIs the input
     * @param thisType the type (used for logging, e.g. STDIN, STDERR)
     */
    public StreamGobbler(final InputStream thisIs, final String thisType) {
        this.is = thisIs;
        this.type = thisType;
    }

    /**
     * Execute the gobbler.
     * 
     * @see java.lang.Runnable#run()
     */
    public final void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;

            while ((line = br.readLine()) != null) {
                log.debug(type + ">" + line);
            }
        }
        catch (IOException ioe) {
            log.warn(type + "> Exception: ", ioe);
        }
    }
}
