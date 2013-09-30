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
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;

import org.zilverline.core.Extractor;
import org.zilverline.core.ParsedFileInfo;
import org.zilverline.util.FileUtils;
import org.zilverline.util.Utils;

/**
 * Abstract baseclass of extractors. Extractors extract all relevant info from a File, and return the info in a ParsedFileInfo
 * Object.
 * 
 * @author Michael Franken
 * @version $Revision: 1.20 $
 * 
 * @see org.zilverline.core.ParsedFileInfo
 */
public abstract class AbstractExtractor implements Extractor {
    /** default size of summary extracted from the file. */
    private static final int SUMMARY_SIZE = 200;

    /**
     * logger for Commons logging. This is non-static final protected, such that it defines a log for all subclasses too.
     */
    protected final Log log = LogFactory.getLog(getClass().getName());

    private final static Log log2 = LogFactory.getLog(AbstractExtractor.class);

    /** default size of summary extracted from the file. */
    private ParsedFileInfo fileInfo = new ParsedFileInfo();

    /**
     * Set the file and all file related information of the document, such as length and modification date.
     * 
     * @param f The file that is being parsed
     */
    public final void setFile(final File f) {
        fileInfo.setFile(f);
        fileInfo.setSize(f.length());
        fileInfo.setModificationDate(f.lastModified());
    }

    /**
     * Set the type of the document.
     * 
     * @param type such as EXCEL, PDF
     */
    public final void setType(final String type) {
        fileInfo.setType(type);
    }

    /**
     * Set the author of the document.
     * 
     * @param author the author
     */
    public final void setAuthor(final String author) {
        fileInfo.setAuthor(author);
    }

    /**
     * Set the isbn number of the document.
     * 
     * @param ISBN the ISBN number
     */
    public final void setISBN(final String ISBN) {
        fileInfo.setISBN(ISBN);
    }

    /**
     * Set the title of the document.
     * 
     * @param title the title
     */
    public final void setTitle(final String title) {
        fileInfo.setTitle(title);
    }

    /**
     * Set the size of the document.
     * 
     * @param size the size in bytes
     */
    public final void setSize(final long size) {
        fileInfo.setSize(size);
    }

    /**
     * Set the modificationDate of the document.
     * 
     * @param modificationDate the modificationDate in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public final void setModificationDate(final long modificationDate) {
        fileInfo.setModificationDate(modificationDate);
    }

    /**
     * Set the creationDate of the document.
     * 
     * @param creationDate the creationDate in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public final void setCreationDate(final long creationDate) {
        fileInfo.setCreationDate(creationDate);
    }

    /**
     * Set the summary of the document.
     * 
     * @param summary the summary
     */
    public final void setSummary(final String summary) {
        fileInfo.setSummary(summary);
    }

    /**
     * Extract the content from the given file. As a side effect other attributes of ParsedFileInfo may be set too.
     * 
     * Implementations should catch all checked exceptions, sensibly, And close all resources.
     * 
     * @param f The file to extract the content from.
     * 
     * @return Reader containing text-only content
     */
    public abstract Reader getContent(final File f);

    /**
     * This method extracts all relevant info of the file as an ParsedFileInfo object. Uses getContent as callback.
     * 
     * @param f the File to extract content from
     * 
     * @return ParsedFileInfo the object containing relevant info of the provided file
     */
    public final ParsedFileInfo extractInfo(final File f) {
        if (f == null) {
            log.warn("Something went terribly wrong, file = null, returning null ");
            return null;
        }
        try {
            setFile(f);

            Reader reader = getContent(f);
            fileInfo.setReader(reader);
            // get the summary from the reader
            if (reader != null) {
                String summary = fileInfo.getSummary();

                if (!StringUtils.hasText(summary)) {
                    char[] sumChars = new char[SUMMARY_SIZE];
                    int numChars = 0;
                    try {
                        if (reader.markSupported()) {
                            reader.mark(SUMMARY_SIZE);
                            numChars = reader.read(sumChars);
                            reader.reset();
                        }
                        if (numChars > 0) {
                            summary = new String(sumChars, 0, numChars);
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Summary extracted from reader: " + summary);
                        }
                        setSummary(getSummaryFromContent(summary));
                    }
                    catch (IOException e) {
                        log.warn("Error extracting summary form reader", e);
                    }
                }
            }
            // Set the title if there's none yet
            if (!StringUtils.hasLength(fileInfo.getTitle())) {
                fileInfo.setTitle(FileUtils.getBasename(f));
            }
        }
        catch (Exception e) {
            // here we don't throw any, since we do not want to interrupt the indexing process
            log.warn("Unexpected Error extracting content from  " + f.getName(), e);
        }
        catch (OutOfMemoryError e) {
            // this happens with very, very large Documents
            log.error("Very Serious Error. Out of Memory for very large documents: " + f.getName()
                + ", try increasing your JVM heap  size: for example, start your server with option '-Xmx128m'."
                + " Skipping file.", e);
        }
        catch (Throwable e) {
            log.error("Very Serious Error while extracting contents from: " + f.getName(), e);
        }

        return fileInfo;
    }

    /**
     * Get a ISBN number from the given text.
     * 
     * @param text the plain text, can be null
     * @return a valid ISBNnumber (10 characters without -) or else ""
     */
    public static String getISBNFromContent(final String text) {
        if (text == null) {
            return "";
        }
        // ISBN:0764543857
        String ISBNnumber = "";
        int j;
        // does text contain ISBN or isbn?
        if (((j = text.indexOf("ISBN")) != -1) || (j = text.indexOf("isbn")) != -1) {
            // look 25 characters forward
            ISBNnumber = text.substring(j, j + 25);
            // remove ISBN.. (all text until first number)
            ISBNnumber = ISBNnumber.replaceFirst("[\\D]+", "");
            // remove all non-valid ISBN characters (0-9xX and - seem valid), remove - as well
            ISBNnumber = ISBNnumber.replaceAll("[^0-9xX]", "");
            if (ISBNnumber.length() > 10) {
                ISBNnumber = ISBNnumber.substring(0, 10);
            }
            log2.debug("possible ISBN found: " + ISBNnumber);
            if (!Utils.isValidISBNNumber(ISBNnumber)) {
                return "";
            }
        }
        return ISBNnumber;
    }

    /**
     * Get a summary from the given text.
     * 
     * @param text the plain text, can be null
     * @return the summary
     */
    public static String getSummaryFromContent(final String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        // alternative: just the first characters:
        String summary = text.substring(0, Math.min(text.length(), SUMMARY_SIZE));
        // SimpleSummariser sum = new SimpleSummariser();
        // get two representative lines
        // String summary = sum.summarise(text, 2);
        // return with minimal whitespace
        return summary.replaceAll("\\s+", " ");
    }
}
