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
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Java bean for the Result of a Parsed File. Used in {@link Extractor}.
 * 
 * @author Michael Franken
 * @version $Revision: 1.6 $
 */
public final class ParsedFileInfo {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(ParsedFileInfo.class);

    /**
     * The file from which the info is extracted.
     */
    private File file;

    /**
     * The reader containing the extracted text.
     */
    private Reader reader;

    /**
     * The author of the document.
     */
    private String author = "";

    /**
     * The ISBN number of the document.
     */
    private String ISBN = "";

    /**
     * The title of the document.
     */
    private String title = "";

    /**
     * The summary of the document.
     */
    private String summary = "";

    /**
     * The creation date of the document.
     */
    private long creationDate;

    /**
     * The modification date of the document.
     */
    private long modificationDate;

    /**
     * The type of the document.
     */
    private String type = "";

    /**
     * The size of the document.
     */
    private long size;

    /**
     * Get the author of the document.
     * 
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get the creation date of the document.
     * 
     * @return the creation data
     */
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * Get the modification date of the document.
     * 
     * @return the modification data
     */
    public long getModificationDate() {
        return modificationDate;
    }

    /**
     * Get the text of the document as a reader.
     * 
     * @return the text of the document
     */
    public Reader getReader() {
        return reader;
    }

    /**
     * Get the summary of the document.
     * 
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Get the type of the document.
     * 
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the author of the document.
     * 
     * @param string the Author
     */
    public void setAuthor(final String string) {
        author = string;
    }

    /**
     * Set the creation date of the document.
     * 
     * @param theDate the date of creation in milliseconds since midnight, January 1, 1970 UTC
     */
    public void setCreationDate(final long theDate) {
        creationDate = theDate;
    }

    /**
     * Set the mofification date of the document.
     * 
     * @param theDate the date of last modification in milliseconds since midnight, January 1, 1970 UTC
     */
    public void setModificationDate(final long theDate) {
        modificationDate = theDate;
    }

    /**
     * Set the reader containing the plain text of this document.
     * 
     * @param thisReader the reader constructed to contain just the text of the document
     */
    public void setReader(final Reader thisReader) {
        this.reader = thisReader;
    }

    /**
     * Set the summary containing the a short summary of this document.
     * 
     * @param string containing
     */
    public void setSummary(final String string) {
        summary = string;
    }

    /**
     * Set the type of this document.
     * 
     * @param string (such as "PDF", "Word")
     */
    public void setType(final String string) {
        type = string;
    }

    /**
     * Get the file from which the info is extracted.
     * 
     * @return file
     */
    public File getFile() {
        return file;
    }

    /**
     * Get the size of the document in bytes.
     * 
     * @return size
     */
    public long getSize() {
        return size;
    }

    /**
     * Get the title of the document.
     * 
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the file containing the document.
     * 
     * @param thisFile file
     */
    public void setFile(final File thisFile) {
        this.file = thisFile;
    }

    /**
     * Set the size of the document.
     * 
     * @param l in bytes
     */
    public void setSize(final long l) {
        size = l;
    }

    /**
     * Set the title of the document.
     * 
     * @param string title
     */
    public void setTitle(final String string) {
        title = string;
    }

    /**
     * Print the file info for logging purposes.
     * 
     * @return string pretty formatted
     */
    public String toString() {
        StringBuffer info = new StringBuffer();

        info.append("File: " + file.getName());
        info.append(", type: " + type);
        info.append(", title: " + title);
        info.append(", author: " + author);
        info.append(", creationDate: " + creationDate);
        info.append(", modificationDate: " + modificationDate);
        info.append(", size: " + size);
        info.append(", summary: " + summary);

        return info.toString();
    }

    /**
     * @return Returns the iSBN.
     */
    public String getISBN() {
        return ISBN;
    }

    /**
     * @param isbn The iSBN to set.
     */
    public void setISBN(String isbn) {
        ISBN = isbn;
    }
}
