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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

/**
 * This class extracts text from text files.
 * 
 * @author Michael Franken
 * @version $Revision: 1.19 $
 */
public class TextExtractor extends AbstractExtractor {

    /**
     * Get the reader form this file, and set the type and summary while we're at it.
     * 
     * @param f the file containing the text
     * @return the reader containing the plain text.
     */
    public final Reader getContent(final File f) {
        setType("TEXT");

        Reader reader = null;
        FileInputStream fis = null;
        try {
            reader = new FileReader(f);
            fis = new FileInputStream(f);
            String content = getContent(fis);
            setSummary(getSummaryFromContent(content));
            setISBN(getISBNFromContent(content));
        }
        catch (FileNotFoundException e) {
            log.warn("Can't extract contents and summary from " + f.getName(), e);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException e1) {
                    log.error("Can't file input stream from " + f.getName(), e1);
                }
            }
        }
        return reader;
    }

    /**
     * Extract the content from the given InputStream.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final String getContent(final InputStream is) {
        int k;
        int aBuffSize = 512;
        byte[] buff = new byte[aBuffSize];
        OutputStream out = new ByteArrayOutputStream(aBuffSize);
        try {
            while ((k = is.read(buff)) != -1) {
                out.write(buff, 0, k);
            }
            return out.toString();
        }
        catch (Exception e) {
            log.warn("Can't extract contents stream ", e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e1) {
                    log.error("Can't close output stream any more", e1);
                }
            }
        }

        return "";
    }
}
