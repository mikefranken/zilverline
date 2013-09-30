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

package org.zilverline.service;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.zilverline.core.DocumentCollection;
import org.zilverline.core.IndexException;
import org.zilverline.dao.DAOException;
import org.zilverline.dao.IndexServiceDAO;

/**
 * Service that indexes collections.
 * 
 * @author Michael Franken
 * @version $Revision: 1.8 $
 */
public class IndexServiceImpl implements IndexService {
    /** logger for commons logging. */
    private static Log log = LogFactory.getLog(IndexServiceImpl.class);

    /** Access to collections, Populated through configuration. */
    private transient CollectionManager collectionManager;

    /** DAO for persisting IndexServiceDAO. */
    private transient IndexServiceDAO dao;

    /**
     * Gets the CollectionManager, which holds all collections, and contains default values for Collection.
     * 
     * @return CollectionManager
     * 
     * @see CollectionManager
     */
    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    /**
     * Sets the CollectionManager, which holds all collections, and contains default values for Collection.
     * 
     * @param cm CollectionManager
     * 
     * @see CollectionManager
     */
    public void setCollectionManager(final CollectionManager cm) {
        collectionManager = cm;
    }

    /**
     * Initializes the IndexService by reading defaults from DAO.
     * 
     */
    public void init() {
        // read the Index settings from DB
        // IndexService tempService = dao.load();
    }

    /**
     * Store the IndexService to store.
     * 
     * @throws IndexException when IndexService can not be stored
     */
    public void store() throws IndexException {
        if (dao != null) {
            try {
                dao.store(this);
            }
            catch (DAOException e) {
                throw new IndexException("Can not save IndexService", e);
            }
        } else {
            throw new IndexException("Can not save IndexService, no DAO set");
        }
    }

    /**
     * Index all Collections incrementally.
     * 
     * @throws IndexException if the Collections can not be indexed
     */
    public void index() throws IndexException {
        this.doIndex(null, false);
    }

    /**
     * Index all Collections fully anew.
     * 
     * @throws IndexException if the Collections can not be indexed
     */
    public void reIndex() throws IndexException {
        this.doIndex(null, true);
    }

    /**
     * Index the given Collections in a background thread.
     * 
     * @param colNames the Names of the Collections, if colNames is empty or null, all Collections will be indexed
     * @param fullIndex indicated whether a full or incremental index should be created
     * @throws IndexException if the Collections can not be indexed
     */
    public void doIndex(final String[] colNames, final boolean fullIndex) throws IndexException {
        if (colNames != null && colNames.length > 0) {
            for (int i = 0; i < colNames.length; i++) {
                DocumentCollection thisCollection = collectionManager.getCollectionByName(colNames[i]);
                if (thisCollection != null) {
                    thisCollection.indexInThread(fullIndex);
                } else {
                    log.warn("No valid collection supplied, can not index: " + colNames[i]);
                }
            }
        } else { // no collection specified, just index all of them
            Iterator iter = collectionManager.getCollections().iterator();
            while (iter.hasNext()) {
                DocumentCollection thisCollection = (DocumentCollection) iter.next();
                thisCollection.indexInThread(fullIndex);
            }
        }
    }

    /**
     * @return Returns the dao.
     */
    public IndexServiceDAO getDao() {
        return dao;
    }

    /**
     * @param thatDao The dao to set.
     */
    public void setDao(final IndexServiceDAO thatDao) {
        this.dao = thatDao;
    }
}
