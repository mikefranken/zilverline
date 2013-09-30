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

package org.zilverline.core;

import java.io.File;

import org.zilverline.service.CollectionManager;

/**
 * DocumentCollection is the base type for all Collections of Documents that are to be indexed by Lucene.
 * 
 * @author Michael Franken
 * @version $Revision: 1.8 $
 */
public interface DocumentCollection {
    /**
     * Get the description of the collection.
     * 
     * @return description for the collection
     */
    String getDescription();

    /**
     * Determines the URL of the collection.
     * <p>
     * The URL maps the contentDir to another location. e.g. A document 'ldap.pdf' in contentDir 'e:\collection\books\' with an URL
     * of 'http://search.company.com/books/' will be returned in a search result as
     * <code>http://search.company.com/books/ldap.pdf</code>
     * </p>
     * 
     * @return the URL of the collection as a String, possibly null in the exeptional case where there is no contentDir
     */
    String getUrlDefault();

    /**
     * Gets the directory where this collection's cache is stored. If the cacheDir is not set for this Collection, the name of this
     * collection is used, possibly prepended with the (default) retrieved from the manager. The cache is used to (temporarily)
     * store expanded content, such as zip files.
     * 
     * @return The directory where the cache of this collection is stored on disk.
     */
    File getCacheDirWithManagerDefaults();

    /**
     * Get the number of documents in this collection. The number is not calculated, but stored after indexing process, so it is a
     * cheap operation.
     * 
     * @return number of documents in collection
     */
    int getNumberOfDocs();

    /**
     * The URL maps the cacheDir to another location.
     * 
     * <p>
     * e.g. A document 'ldap.pdf' in cacheDir 'e:\collection\cache\books\' with an cacheURL of
     * 'http://search.company.com/cachedBooks/' will be returned in a search result as
     * <code>http://search.company.com/cachedBooks/ldap.pdf</code>
     * </p>
     * 
     * @return the cacheUrl of the collection, or the cacheDir as URL if url is null or empty.
     */
    String getCacheUrlWithManagerDefaults();

    /**
     * 'Calculates' the directory where the index of this collection is stored on disk. If the indexDir is not set for this
     * Collection, the name of this collection is used, possibly prepended with the baseDir retrieved from the manager.
     * 
     * @return The directory where the index of this collection is stored on disk, never null
     */
    File getIndexDirWithManagerDefaults();

    /**
     * Determines whether the cache containing archive's contents should be kept after being indexed. It does so by retrieving the
     * defaults from the manager if needed.
     * 
     * @return true if so.
     */
    boolean isKeepCacheWithManagerDefaults();

    /**
     * Check whether the index of this collection is valid. An index is valid when the directory exists and there is an index in it.
     * 
     * @return true if the index is valid, otherwise false.
     * 
     * @throws IndexException when existing index of Collection can not be succesfully opened.
     */
    boolean isIndexValid() throws IndexException;

    /**
     * Get the id of the collection.
     * 
     * @return unique id, can be null
     */
    Long getId();

    /**
     * Get the name of this collection.
     * 
     * @return name of collection
     */
    String getName();

    /**
     * Indicates whether any indexing is going on.
     * 
     * @return true if so.
     */
    boolean isIndexingInProgress();

    /**
     * Index the given Collection.
     * 
     * @param fullIndex indicated whether a full or incremental index should be created
     * @throws IndexException if the Collections can not be indexed
     */
    void index(boolean fullIndex) throws IndexException;

    /**
     * Index the given Collection.
     * 
     * @param fullIndex indicated whether a full or incremental index should be created
     * @throws IndexException if the Collections can not be indexed
     */
    void indexInThread(boolean fullIndex) throws IndexException;

    /**
     * Set the id of the collection. The id is used by the collectionManager to add and retrieve collections.
     * 
     * @param theId the Id
     */
    void setId(Long theId);

    /**
     * Initialize this collection by getting its index. It retrieves the number of documents and the MD5 hash of all documents in
     * the collection.
     * 
     * If the index does not exist (this is a new Collection) just return.
     * 
     * @throws IndexException when existing index of Collection can not be succesfully opened.
     */
    void init() throws IndexException;

    /**
     * Set the collectionManager.
     * 
     * @param thisManager The CollectionManager holding this collection.
     */
    void setManager(CollectionManager thisManager);
}
