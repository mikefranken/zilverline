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

import junit.framework.TestCase;

/**
 * DOCUMENT ME!
 * 
 * @author michael To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class TestStopWatch extends TestCase {
    /**
     * Creates a new TestStopWatch object.
     * 
     * @param arg0 DOCUMENT ME!
     */
    public TestStopWatch(String arg0) {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     * 
     * @todo This methods needs documentation.
     */
    public void testStopWatch() {
        try {
            StopWatch watch = new StopWatch();

            watch.start();

            // elapsedTime without stop
            assertTrue(watch.elapsedTime().matches("\\d+ milliseconds"));

            // wait 2 seconds
            // Thread.sleep(2000);
            // elapsedTime without stop
            // assertTrue(watch.elapsedTime().matches("\\d+ seconds and \\d+
            // milliseconds"));
            // wait 2 seconds
            // Thread.sleep(2000);
            // watch.stop();
            // elapsedTime after stop
            // assertTrue(watch.elapsedTime().matches("\\d+ seconds and \\d+
            // milliseconds"));
            // elapsedTime after restart
            // watch.start();
            // watch.stop();
            // elapsedTime after stop
            // assertTrue(watch.elapsedTime().matches("\\d+ milliseconds"));
            // watch.start();
            // wait a minute, comment out if you dont want to wait so long
            // Thread.sleep(60000);
            // watch.stop();
            // assertTrue(watch.elapsedTime().matches("\\d minutes and \\d
            // seconds"));
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
