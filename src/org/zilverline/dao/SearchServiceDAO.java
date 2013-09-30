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

package org.zilverline.dao;

import org.zilverline.service.SearchServiceImpl;

/**
 * Interface towards data access of a SearchService.
 * 
 * @author Michael Franken
 * @version $Revision: 1.6 $
 */
public interface SearchServiceDAO {
    /**
     * Retrieve the SearchService from store.
     * 
     * @return SearchService the service
     */
    SearchServiceImpl load();

    /**
     * Save the SearchService to the datastore.
     * 
     * @param service the SearchService
     * @throws DAOException if the service can not be stored
     */
    void store(SearchServiceImpl service) throws DAOException;

}
