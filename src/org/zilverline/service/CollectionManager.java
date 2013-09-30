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

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;

import org.zilverline.core.DocumentCollection;
import org.zilverline.core.ExtractorFactory;
import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.Handler;
import org.zilverline.core.IndexException;

/**
 * The CollectionManager holds all collections, and base values for them.
 * 
 * @author Michael Franken
 * @version $Revision: 1.16 $
 * 
 * @since 18 september 2004
 */
public interface CollectionManager {
    /**
     * @return Returns the analyzer.
     */
    String getAnalyzer();

    /**
     * Deletes collection from list of collections.
     * 
     * @param col Collection containing documents
     * 
     */
    void deleteCollection(DocumentCollection col);

    /**
     * @return Returns the allAnalyzers.
     */
    String[] getAllAnalyzers();

    /**
     * @return Returns the allExtractors.
     */
    String[] getAllExtractors();

    /**
     * Add collection to list of collections.
     * 
     * @param col Collection containing documents
     */
    void addCollection(final DocumentCollection col);

    /**
     * Get the cache base directory.
     * 
     * @return String the directory where the cache sits
     */
    File getCacheBaseDir();

    /**
     * Gets a collection by id.
     * 
     * @param theId The id of the collection
     * 
     * @return Collection or null if not found
     */
    DocumentCollection getCollection(final Long theId);

    /**
     * Gets a collection by name.
     * 
     * @param theName The name of the collection
     * 
     * @return Collection or null if not found
     */
    DocumentCollection getCollectionByName(final String theName);

    /**
     * Get all collections.
     * 
     * @return collections List of collections
     */
    List getCollections();

    /**
     * Get the base directory for the index.
     * 
     * @return the directory
     */
    File getIndexBaseDir();

    /**
     * Initializes all collections.
     * 
     * @throws IndexException if the Collection can not be initialized or retrieved from store.
     */
    void init() throws IndexException;

    /**
     * The default for all collections whether to keep cache dir after indexing.
     * 
     * @return whether to keep the cache or not.
     */
    boolean isKeepCache();

    /**
     * The default cache base directory for all collections. The cache is the directory on disk where zipped content is unzipped for
     * indexing.
     * 
     * @param thisDir the directory on disk
     */
    void setCacheBaseDir(final File thisDir);

    /**
     * The default index base directory for all collections. The index is the directory on disk where a Lucene index is stored.
     * 
     * @param thisDir the directory on disk
     * 
     * @see org.apache.lucene.index.IndexReader
     */
    void setIndexBaseDir(final File thisDir);

    /**
     * Indicates whether a Collection cache should be kept after indexing. The value of this CollectionManagerImpl functions as
     * default for all Collections.
     * 
     * @param b keep cache or not.
     */
    void setKeepCache(final boolean b);

    /**
     * Indicates whether any indexing is going on.
     * 
     * @return true if so.
     */
    boolean isIndexingInProgress();

    /**
     * Returns an Analyzer for this collection based on configuration.
     * 
     * @return the Analyzer used to index and search this collection
     */
    Analyzer createAnalyzer();

    /**
     * Create an Analyzer as specified by the given String.
     * 
     * @param analyzerClassName the name of the class. The class needs to be available on the classpath.
     */
    void setAnalyzer(final String analyzerClassName);

    /**
     * Store the CollectionManager to store.
     * 
     * @throws IndexException when collectionManager can not be saved to underlying store
     */
    void store() throws IndexException;

    /**
     * get the ArchiveHandler, which contains the mappings for unArchiving archives.
     * 
     * @return object containing mappings for handling archives
     */
    Handler getArchiveHandler();

    /**
     * @return Returns the factory.
     */
    ExtractorFactory getFactory();

    /**
     * Set the ArchiveHandler.
     * 
     * @param handler object containing mappings for handling archives
     */
    void setArchiveHandler(final Handler handler);

    /**
     * @param thatFactory The factory to set.
     */
    void setFactory(final ExtractorFactory thatFactory);

    /**
     * @return Returns the mergeFactor.
     */
    Integer getMergeFactor();

    /**
     * @param mergeFactor The mergeFactor to set.
     */
    void setMergeFactor(Integer mergeFactor);

    /**
     * @return Returns the priority.
     */
    Integer getPriority();

    /**
     * @param priority The priority to set.
     */
    void setPriority(Integer priority);

    /**
     * @return Returns the maxMergeDocs.
     */
    Integer getMaxMergeDocs();

    /**
     * @param maxMergeDocs The maxMergeDocs to set.
     */
    void setMaxMergeDocs(Integer maxMergeDocs);

    /**
     * @return Returns the minMergeDocs.
     */
    Integer getMinMergeDocs();

    /**
     * @param minMergeDocs The minMergeDocs to set.
     */
    void setMinMergeDocs(Integer minMergeDocs);

    /**
     * Expands Archive to disk. This is used is 'on-the-fly' extraction from cache
     * 
     * @param col the Collection to which cache this archive is extracted
     * @param zip the archive
     * 
     * @return true if archive could be extracted
     * 
     * @throws IndexException
     * 
     * @see org.zilverline.web.CacheController
     */
    boolean expandArchive(final FileSystemCollection col, final File zip) throws IndexException;

    /**
     * 'unpacks' a given archive file into cache directory with derived name. e.g. c:\temp\file.chm wil be unpacked into
     * [cacheDir]\file_chm\.
     * 
     * @param sourceFile the Archive file to be unpacked
     * @param thisCollection the collection whose cache and contenDir is used
     * 
     * @return File (new) directory containing unpacked file, null if unknown Archive
     */
    File unPack(final File sourceFile, final FileSystemCollection thisCollection);
}
