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
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;

import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;

/**
 * This class extracts text from pdf files by using the PdfBox library. Inspired/Copied from
 * org.pdfbox.searchengine.lucene.LucenePDFDocument
 * 
 * @author Ben Litchfield, Michael Franken
 * @version $Revision: 1.26 $
 */
public class PDFExtractor extends AbstractExtractor {
    /**
     * Extract the content from the given PDF file. As a side effect most other fields are set too.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final Reader getContent(final File f) {
        setType("PDF");

        Reader reader = null;
        PDDocument pdfDocument = null;
        FileInputStream fis = null;
        try {
            log.debug("Getting contents from PDF: " + f.getName());
            fis = new FileInputStream(f);
            PDFParser parser = new PDFParser(fis);
            parser.parse();
            pdfDocument = parser.getPDDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            String contents = stripper.getText(pdfDocument);
            reader = new StringReader(contents);

            // Add the summary
            setSummary(getSummaryFromContent(contents));
            setISBN(getISBNFromContent(contents));

            log.debug("Getting info from PDF: " + f.getName());
            PDDocumentInformation info = pdfDocument.getDocumentInformation();
            if (info.getAuthor() != null) {
                setAuthor(info.getAuthor());
            }
            if (info.getCreationDate() != null) {
                Date date = info.getCreationDate().getTime();
                setCreationDate(date.getTime());
            }
            if (info.getModificationDate() != null) {
                Date date = info.getModificationDate().getTime();
                setModificationDate(date.getTime());
            }
            if (info.getTitle() != null) {
                setTitle(info.getTitle());
            }
        }
        catch (IOException e) {
            log.warn("Error: Can't open file: " + f.getName(), e);
        }
        catch (ClassCastException e) {
            // TODO: get fix for
            // http://sourceforge.net/tracker/index.php?func=detail&aid=921745&group_id=78314&atid=552832
            log.debug("A bug in PDFBox?, can't decrypt: " + f.getName(), e);
        }
        catch (Throwable e) {
            // catch this, since we need to close the resources
            log.debug("An error occurred while getting contents from PDF: " + f.getName(), e);
        }
        finally {
            if (pdfDocument != null) {
                try {
                    log.debug("Closing file: " + f.getName());
                    pdfDocument.close();
                }
                catch (IOException e1) {
                    log.warn("Error: Can't close file.", e1);
                }
            }
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
     * Extract the content from the given PDF file. As a side effect most other fields are set too.
     * 
     * @see org.zilverline.extractors.AbstractExtractor#getContent(java.io.File)
     */
    public final String getContent(final InputStream is) {
        PDDocument pdfDocument = null;
        try {
            log.debug("Getting contents from PDF");
            PDFParser parser = new PDFParser(is);
            parser.parse();
            pdfDocument = parser.getPDDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdfDocument);
        }
        catch (IOException e) {
            log.warn("Error: Can't open stream", e);
        }
        catch (Throwable e) {
            // catch this, since we need to close the resources
            log.debug("An error occurred while getting contents from PDF", e);
        }
        finally {
            if (pdfDocument != null) {
                try {
                    pdfDocument.close();
                }
                catch (IOException e1) {
                    log.warn("Error: Can't close pdf.", e1);
                }
            }
        }

        return "";
    }
}
