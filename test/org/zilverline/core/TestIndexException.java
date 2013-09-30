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

package org.zilverline.core;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DOCUMENT ME!
 * 
 * @author michael To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class TestIndexException extends TestCase {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(TestIndexException.class);

    /**
     * Creates a new TestCollection object.
     * 
     * @param arg0 DOCUMENT ME!
     */
    public TestIndexException(String arg0) {
        super(arg0);
    }

    /**
     * This methods increases unit test coverage.
     */
    public void testException() {
        try {
            throw new IndexException("Error in Index");
        }
        catch (IndexException ie) {
            assertEquals("Error in Index", ie.getMessage());
        }
    }

    /**
     * This methods tests ServletException and 1.4 Throwable cause hierarchy.
     */
    public void testExceptionHierarchy() {
        try {
            try {
                throw new Exception("Error");
            }
            catch (Exception e) {
                try {
                    throw new IndexException("Error in Index", e);
                }
                catch (IndexException ie) {
                    throw new ServletException("Error in Servlet", ie);
                }
            }
        }
        catch (ServletException se) {
            Throwable rootCause = se.getRootCause();
            Throwable seCause = se.getCause();

            assertNotNull(rootCause);
            assertNull(seCause);
            assertEquals("Error in Index", rootCause.getMessage());

            Throwable cause = rootCause.getCause();

            assertNotNull(cause);
            assertEquals("Error", cause.getMessage());
            assertEquals("Error in Servlet", se.getMessage());
        }
    }
}
