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

package org.zilverline.extractors;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

/**
 * Does not extract content from file.
 * 
 * @author Michael Franken
 * @version $Revision: 1.2 $
 */
public class FileInfoExtractor extends AbstractExtractor {

    /**
     * Don't get the reader from this file, and set the type to "FILE".
     * 
     * @param f the file containing the text
     * @return the reader containing the plain text.
     */
    public final Reader getContent(final File f) {
        setType("FILE");

        return null;
    }

    /**
     * Don't extract the content.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final String getContent(final InputStream is) {
        return "";
    }
}
