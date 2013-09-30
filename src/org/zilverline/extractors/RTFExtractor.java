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

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * This class extracts text from RTF files by using the javax.swing.text.rtf library.
 * 
 * @author Michael Franken
 * @version $Revision: 1.15 $
 */
public class RTFExtractor extends AbstractExtractor {
    /**
     * Extract the content from the given RTF file. As a side effect the type and summary are set too.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final Reader getContent(final File f) {
        setType("RTF");

        Reader reader = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            RTFEditorKit kit = new RTFEditorKit();
            Document doc = kit.createDefaultDocument();

            kit.read(fis, doc, 0);

            String plainText = doc.getText(0, doc.getLength());

            reader = new StringReader(plainText);

            setSummary(getSummaryFromContent(plainText));
            setISBN(getISBNFromContent(plainText));
        }
        catch (FileNotFoundException e) {
            log.warn("Can't open file: " + f.getName(), e);
        }
        catch (IOException e) {
            log.warn("Can't extract contents for: " + f.getName(), e);
        }
        catch (BadLocationException e) {
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
     * Extract the content from the given RTF file. As a side effect the type and summary are set too.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final String getContent(final InputStream is) {
        try {
            RTFEditorKit kit = new RTFEditorKit();
            Document doc = kit.createDefaultDocument();

            kit.read(is, doc, 0);

            return doc.getText(0, doc.getLength());

        }
        catch (IOException e) {
            log.warn("Can't extract contents", e);
        }
        catch (BadLocationException e) {
            log.warn("Can't extract contents", e);
        }

        return "";
    }
}
