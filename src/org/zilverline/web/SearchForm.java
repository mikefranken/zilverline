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

import java.util.Date;

/**
 * Form for submitting search requests.
 * 
 * @author Michael Franken
 * @version $Revision: 1.24 $
 */
public final class SearchForm {
    /**
     * Constructor sets defaults.
     */
    public SearchForm() {
        startAt = 1;
        maxResults = 20;
        query = "Enter your query here";
    }

    /** Collection(s) participating in search. */
    private CollectionTriple[] collections;

    /** Query of search. */
    private String query;

    /** Maximum number of results the search should return. */
    private int maxResults;

    /**
     * Number of results to start returning results from e.g. if startAt = 20 and numResultsPerPage = 20 then return 21 to 40.
     */
    private int startAt;

    /**
     * Starting date from which to return documents.
     */
    private Date startDate;

    /**
     * Ending date until which to return documents.
     */
    private Date endDate;

    /**
     * Get the submitted query.
     * 
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the submitted query.
     * 
     * @param theQuery the query
     */
    public void setQuery(final String theQuery) {
        query = theQuery;
    }

    /**
     * Get the maximum number of results that the search should return (per page).
     * 
     * @return the maximum number of results
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Set the maximum number of results that the search should return (per page).
     * 
     * @param i the maximum number of results
     */
    public void setMaxResults(final int i) {
        maxResults = i;
    }

    /**
     * Get the number to start returning results from. e.g. if startAt = 20 and numResultsPerPage = 20 then return 21 to 40
     * 
     * @return the number to start returning results from
     */
    public int getStartAt() {
        return startAt;
    }

    /**
     * Set the number to start returning results from. e.g. if startAt = 20 and numResultsPerPage = 20 then return 21 to 40
     * 
     * @param resultNumber the result number to start returning results from
     */
    public void setStartAt(final int resultNumber) {
        if (resultNumber <= 0) {
            startAt = 1;
        } else {
            startAt = resultNumber;
        }
    }

    /**
     * @return Returns the collection triples.
     */
    public CollectionTriple[] getCollections() {
        return collections;
    }

    /**
     * @param triples The collection triples to set.
     */
    public void setCollections(final CollectionTriple[] triples) {
        collections = triples;
    }

    /**
     * @return Returns the endDate.
     */
    public final Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate The endDate to set.
     */
    public final void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return Returns the startDate.
     */
    public final Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate The startDate to set.
     */
    public final void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
