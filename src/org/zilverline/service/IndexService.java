/*
 * Copyright 2003-2005 Michael Franken, Zilverline.
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

package org.zilverline.service;

import org.zilverline.core.IndexException;

/**
 * IndexServiceInterface
 * 
 * @author Michael Franken
 */
public interface IndexService {
    /**
     * Index all Collections incrementally.
     * 
     * @throws IndexException if the Collections can not be indexed
     */
    public void index() throws IndexException;

    /**
     * Index all Collections fully anew.
     * 
     * @throws IndexException if the Collections can not be indexed
     */
    public void reIndex() throws IndexException;

    /**
     * Index the given Collections.
     * 
     * @param colNames the Names of the Collections, if colNames is empty or null, all Collections will be indexed
     * @param fullIndex indicated whether a full or incremental index should be created
     * @throws IndexException if the Collections can not be indexed
     */
    public void doIndex(final String[] colNames, final boolean fullIndex) throws IndexException;

}
