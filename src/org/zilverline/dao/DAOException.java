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

package org.zilverline.dao;

/**
 * An exception that indicates a DAO can not retrieve or store an object.
 * 
 * @author Michael Franken
 * @version $Revision: 1.3 $
 * 
 */
public class DAOException extends Exception {
    /**
     * The <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 3257853185938044983L;

    /**
     * Constructs a <code>IndexException</code> with the specified message and no root cause.
     * 
     * @param msg the detail message.
     */
    public DAOException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>IndexException</code> with the specified message and root cause.
     * 
     * @param msg the detail message.
     * @param t the root cause
     */
    public DAOException(final String msg, final Throwable t) {
        super(msg, t);
    }
}
