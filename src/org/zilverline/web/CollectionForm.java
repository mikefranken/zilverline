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

package org.zilverline.web;

/**
 * Form for submitting collections to indexing.
 * 
 * @author Michael Franken
 * @version $Revision: 1.15 $
 */
public final class CollectionForm {

    /** name(s) of collection(s). */
    private String[] names;

    /** indicates whether the index needs to be reindexed instead of incrementally. */
    private boolean fullIndex;

    /**
     * Get the Array of name(s) of the collection(s).
     * 
     * @return the names of the Collection
     */
    public String[] getNames() {
        return names;
    }

    /**
     * Set the name(s) of the collection(s).
     * 
     * @param string name(s) of the collection(s)
     */
    public void setNames(final String[] string) {
        names = string;
    }

    /**
     * Check whether the index needs to be reindexed instead of incrementally.
     * 
     * @return true if so
     */
    public boolean getFullIndex() {
        return fullIndex;
    }

    /**
     * Set whether the index needs to be reindexed instead of incrementally.
     * 
     * @param b true of false
     */
    public void setFullIndex(final boolean b) {
        fullIndex = b;
    }
}
