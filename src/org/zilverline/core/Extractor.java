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

import java.io.File;
import java.io.InputStream;

/**
 * This interface defines the type of family of extractors. Extractors extract all relevant info from a File, and return the info in
 * a ParsedFileInfo Object. These are mappings used by zilverline to plugin extractors based on file extensions. The plugin is a
 * java class that implements the Extractor interface and needs to be available on the classpath.
 * 
 * <p>
 * So if for example you specify the mapping "pdf => org.zilverline.extractors.PDFExtractor" make sure
 * org.zilverline.extractors.PDFExtractor is available, otherwise an Exception will be raised and handled by zilverline.
 * </p>
 * 
 * <p>
 * Right now you can use the TEXT, HTML, WORD, EXCEL, POWERPOINT and PDF extractors, and define the extensions you want to map. You
 * can not use wildcards, but you can define multiple extensions for one Extractor. By default the extensions are treated case
 * insensitively, but you can change that. Note that you van use an empty extension as well.
 * </p>
 * 
 * @author Michael Franken
 * @version $Revision: 1.5 $
 * 
 * @see org.zilverline.core.ParsedFileInfo
 */
public interface Extractor {
    /**
     * This method extracts all relevant info of the file as an ParsedFileInfo object.
     * 
     * @param f the File to extract content from
     * 
     * @return ParsedFileInfo the object containing relevant info of the provided file
     */
    ParsedFileInfo extractInfo(final File f);

    /**
     * Extract the content from the given InputStream.
     * 
     */
    String getContent(final InputStream is);

    // /**
    // * Extract the content from the given String.
    // *
    // */
    // String getContent(final String s);

}
