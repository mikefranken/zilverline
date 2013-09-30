/*
 * Copyright 2003-2005 Michael Franken, Zilverline.
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

package org.zilverline.service;

import org.zilverline.core.SearchException;
import org.zilverline.core.SearchResult;
import org.zilverline.dao.SearchServiceDAO;
import org.zilverline.lucene.BoostFactor;

/**
 * Service that searches collections.
 * 
 * @author Michael Franken
 * @version $Revision: 1.15 $
 */
public interface SearchService {
    /**
     * @return Returns the factors.
     */
    BoostFactor getFactors();

    /**
     * @param thoseFactors The factors to set.
     */
    void setFactors(BoostFactor thoseFactors);

    /**
     * Gets the CollectionManager, which holds all collections, and contains default values for Collection.
     * 
     * @return CollectionManager
     * 
     * @see CollectionManager
     */
    CollectionManager getCollectionManager();

    /**
     * @return Returns the dao.
     */
    SearchServiceDAO getDao();

    /**
     * Get the maximum number of results returned by a query.
     * 
     * @return the number of results
     */
    int getMaxResults();

    /**
     * Gets the default search query.
     * 
     * @return the query
     */
    String getQuery();

    /**
     * Initializes the SearchService by reading defaults from DAO.
     * 
     */
    void init();

    /**
     * Sets the CollectionManager, which holds all collections, and contains default values for Collection.
     * 
     * @param cm CollectionManager
     * 
     * @see CollectionManager
     */
    void setCollectionManager(CollectionManager cm);

    /**
     * @param thisDao The dao to set.
     */
    void setDao(SearchServiceDAO thisDao);

    /**
     * Set the maximum number of results returned by a query.
     * 
     * @param max value for the number of results.
     */
    void setMaxResults(int max);

    /**
     * Set the default query.
     * 
     * @param thisQuery the default search query.
     */
    void setQuery(String thisQuery);

    /**
     * Store the SearchService to store.
     * 
     * @throws SearchException on error
     */
    void store() throws SearchException;

    /**
     * Searches the given Collections for the query. Sending an empty or null array of names will betreated as if all collections
     * need to be searched, which is handy for external queries, that can't know the names of collections.
     * 
     * @param names array of collection names
     * @param queryString the query
     * @param startAt the first result to return (start counting from 0)
     * @param numberOfResults the (maximum) number of results to return
     * @return Object containing the results, and number of hits and the possibly changed startAt and endAt
     * 
     * @throws SearchException if query can't be executed
     */
    SearchResult doSearch(String[] names, String queryString, int startAt, int numberOfResults) throws SearchException;

    /**
     * @return Returns the allBoostableFields.
     */
    String[] getAllBoostableFields();
}
