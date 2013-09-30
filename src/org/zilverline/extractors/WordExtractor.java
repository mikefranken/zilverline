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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * This class extracts text from MS Word files by using the textmining library.
 * 
 * @author Michael Franken
 * @version $Revision: 1.20 $
 */
public class WordExtractor extends AbstractExtractor {
    /**
     * Extract the content from the given Word file. As a side effect the type and summary are set too.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final Reader getContent(final File f) {
        Reader reader = null;
        FileInputStream fis = null;
        setType("WORD");

        org.textmining.text.extraction.WordExtractor wex = new org.textmining.text.extraction.WordExtractor();
        String text = "";

        try {
            fis = new FileInputStream(f);
            text = wex.extractText(fis);
            reader = new StringReader(text);

            setSummary(getSummaryFromContent(text));
            setISBN(getISBNFromContent(text));
        }
        catch (FileNotFoundException e) {
            log.warn("Can't open file: " + f.getName(), e);
        }
        catch (Exception e) {
            log.warn("Can't extract contents for: " + f.getName(), e);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException e1) {
                    log.warn("Can not close inputstream for " + f.getName(), e1);
                }
            }
        }

        return reader;
    }

    /**
     * Extract the content from the given Word InputStream.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final String getContent(final InputStream is) {
        org.textmining.text.extraction.WordExtractor wex = new org.textmining.text.extraction.WordExtractor();

        try {
            return wex.extractText(is);
        }
        catch (Exception e) {
            log.warn("Can't extract contents stream ", e);
        }

        return "";
    }
}
