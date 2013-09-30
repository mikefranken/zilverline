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
 * Form for submitting a document in an archive to be extracted from archive.
 * 
 * @author Michael Franken
 * @version $Revision: 1.6 $
 */
public class DocumentFromCacheForm {
    private String collection;

    private String archive;

    private String file;

    /**
     * Get the String for the name(s) of the collection(s).
     * 
     * @return the name of the Collection
     */
    public final String getCollection() {
        return collection;
    }

    /**
     * Returns the location of the file as a String.
     * 
     * @return the file location
     */
    public final String getFile() {
        return file;
    }

    /**
     * Returns the name of the archive as a String.
     * 
     * @return the file location
     */
    public final String getArchive() {
        return archive;
    }

    /**
     * Set the name of the collection.
     * 
     * @param string name of the collection
     */
    public final void setCollection(final String string) {
        collection = string;
    }

    /**
     * Sets the location of the file.
     * 
     * @param thisFile the file location
     */
    public final void setFile(final String thisFile) {
        file = thisFile;
    }

    /**
     * Sets the name of the archive.
     * 
     * @param thisArchive the file location
     */
    public final void setArchive(final String thisArchive) {
        archive = thisArchive;
    }
}
