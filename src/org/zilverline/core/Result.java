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

import java.text.NumberFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Result is a individual hit result from a search. It corresponds to a {@link org.apache.lucene.document.Document Document}.
 * 
 * <p>
 * This class is used to represent a Result in the model of our MVC pattern.
 * </p>
 * 
 * @author Michael Franken
 * @version $Revision: 1.17 $
 * 
 * @see org.apache.lucene.document.Document
 */
public class Result {
    /** 1024 is the number of bytes that goes into a KB. */
    private static final int KILO = 1024;

    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(Result.class);

    /** cache of result (= document). */
    private String cache;

    /** name of collection this result is from. */
    private String collection;

    /** date of result (= document). */
    private Date modificationDate;

    /** name of result (= document). */
    private String name;

    /** path within this document. */
    private String path;

    /** score of result. */
    private float score;

    /** score of result as a percentage String 0.13 => 13,00%. */
    private String scoreString;

    /** size of in bytes as String. */
    private String size;

    /** summary of result (= document). */
    private String summary;

    /** title of result (= document). */
    private String title;

    /** type of Document: pdf, word. */
    private String type;

    /** url of result (= document). */
    private String url;

    /** name within this document. */
    private String zipName;

    /** path within this document. */
    private String zipPath;

    /**
     * The ISBN number of the document.
     */
    private String ISBN = "";

    /**
     * @return Returns the ISBN number.
     */
    public String getISBN() {
        return ISBN;
    }

    /**
     * @param isbn The ISBN number to set.
     */
    public void setISBN(String isbn) {
        ISBN = isbn;
    }

    /**
     * Default Constructor setting all fields to non null defaults.
     */
    public Result() {
        name = "";
        title = "";
        type = "";
        score = 0;
        scoreString = "";
        size = "";
        path = "";
        zipPath = "";
        summary = "";
        modificationDate = null;
    }

    /**
     * Get the cache of this result.
     * 
     * @return the Cache
     */
    public final String getCache() {
        return cache;
    }

    /**
     * Get the collection of this result.
     * 
     * @return the collection
     */
    public final String getCollection() {
        return collection;
    }

    /**
     * Get the modificationDate of this result.
     * 
     * @return String modification date of the result.
     */
    public final Date getModificationDate() {
        return modificationDate;
    }

    /**
     * Get the name of this result.
     * 
     * @return the name of this result.
     */
    public final String getName() {
        return name;
    }

    /**
     * Get the path of this result.
     * 
     * @return the path of this result. e.g. chapter24/chapter24.html
     */
    public final String getPath() {
        return path;
    }

    /**
     * Get the score of this result.
     * 
     * @return the score of the result as a float between 0 and 1.
     */
    public final float getScore() {
        return score;
    }

    /**
     * Get the string representation of the score in percentage.
     * 
     * @return String string representation of score. Includes percentage sign, e.g. 13%
     */
    public final String getScoreString() {
        return scoreString;
    }

    /**
     * Get the size of the result.
     * 
     * <p>
     * use getSizeAsString to get a human friendly version
     * </p>
     * 
     * @return String size of result (= document) as a String.
     */
    public final String getSize() {
        return size;
    }

    /**
     * Get the human friendly representation of the size of the result.
     * 
     * <p>
     * returns B, KB, MB, GB, e.g. 12345 returns 12KB, B = 1024 bytes
     * </p>
     * 
     * @return String the size in bytes in human readable format, "" if unknown.
     */
    public final String getSizeAsString() {
        try {
            int byteSize = Integer.parseInt(size);

            if ((byteSize / (KILO * KILO * KILO)) > 0) { // GB

                return (byteSize / (KILO * KILO * KILO)) + "GB";
            } else if ((byteSize / (KILO * KILO)) > 0) { // MB

                return (byteSize / (KILO * KILO)) + "MB";
            } else if ((byteSize / KILO) > 0) { // KB

                return (byteSize / KILO) + "KB";
            }

            return byteSize + "B";
        }
        catch (NumberFormatException e) {
            return "";
        }
    }

    /**
     * Get the summary of the result.
     * 
     * @return a summary of the result, if any
     */
    public final String getSummary() {
        return summary;
    }

    /**
     * Get the title of the result.
     * 
     * @return title of the result
     */
    public final String getTitle() {
        return title;
    }

    /**
     * Get the type of the result.
     * 
     * @return String the type of the document, such as PDF, WORD.
     */
    public final String getType() {
        return type;
    }

    /**
     * Get the URL of the result.
     * 
     * @return url
     */
    public final String getURL() {
        return url;
    }

    /**
     * Get the zipName of the result.
     * 
     * @return String the zipName
     */
    public final String getZipName() {
        return zipName;
    }

    /**
     * Get the zpPath of the result.
     * 
     * @return String the zipPath
     */
    public final String getZipPath() {
        return zipPath;
    }

    /**
     * Set the cache of the result.
     * 
     * @param string cache
     */
    public final void setCache(final String string) {
        cache = string;
    }

    /**
     * Set the collection of the result.
     * 
     * @param string collection by name
     */
    public final void setCollection(final String string) {
        collection = string;
    }

    /**
     * Set the modification date of the result.
     * 
     * @param theModificationDate the modification date of the result.
     */
    public final void setModificationDate(final Date theModificationDate) {
        modificationDate = theModificationDate;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param theName the path of this result. e.g. j2ee.zip
     */
    public final void setName(final String theName) {
        name = theName;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param thePath the path of this result. e.g. http://search.company.com/books/ldap.pdf
     */
    public final void setPath(final String thePath) {
        path = thePath;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param theScore float sets the score, and set scoreString to the String representation of the score as a percentage, e.g.
     *            13%.
     */
    public final void setScore(final float theScore) {
        score = theScore;

        NumberFormat percentFormatter = NumberFormat.getPercentInstance();

        scoreString = percentFormatter.format(theScore);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param theSize the size of the result as a String. the size is in bytes
     */
    public final void setSize(final String theSize) {
        size = theSize;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param theSummary sets the summary of the result.
     */
    public final void setSummary(final String theSummary) {
        summary = theSummary;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param theTitle sets the summary of the result.
     */
    public final void setTitle(final String theTitle) {
        title = theTitle;
    }

    /**
     * Set the type of the result.
     * 
     * @param theType the type of the document, such as PDF, WORD.
     */
    public final void setType(final String theType) {
        type = theType;
    }

    /**
     * Set the URL of this result.
     * 
     * @param string url
     */
    public final void setURL(final String string) {
        url = string;
    }

    /**
     * Set the zipName of the result.
     * 
     * @param string zipName is the name within the archive
     */
    public final void setZipName(final String string) {
        zipName = string;
    }

    /**
     * Set the sipPath of the result.
     * 
     * @param string zipPath is the path within the archive
     */
    public final void setZipPath(final String string) {
        zipPath = string;
    }
}
