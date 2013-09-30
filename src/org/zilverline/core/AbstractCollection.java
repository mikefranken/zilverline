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
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import org.zilverline.service.CollectionManager;
import org.zilverline.util.FileUtils;

/**
 * AbstractCollection provides common implementation for all
 * DocumentCollections.
 * 
 * @author Michael Franken
 * @version $Revision: 1.12 $
 */
public abstract class AbstractCollection implements DocumentCollection {
	/** logger for Commons logging. */
	private static Log log = LogFactory.getLog(AbstractCollection.class);

	/**
	 * String representation of Analyzer.
	 */
	protected String analyzer;

	/** The Analyzer to be used in indexing and searching. */
	protected transient Analyzer analyzerObject = null;

	/**
	 * The archive cache is used to store the keys of archives that are
	 * extracted 'on-the-fly'.
	 */
	protected transient Set archiveCache;

	/**
	 * The cacheDir is the directory this collection's cache is stored at.
	 * 
	 * <p>
	 * The cache is used to (temporarily) store expanded content, such as zip
	 * files.
	 * </p>
	 */
	protected File cacheDir;

	/**
	 * The cacheUrl is the location this collection's cache (if any) is mapped
	 * to as a result of a search. e.g. d:\temp\cache\books\java could be mapped
	 * to https://server/cache/path
	 */
	protected String cacheUrl;

	/**
	 * The contentDir is the directory this collection is stored at.
	 * <p>
	 * e.g. d:\books\java
	 * </p>
	 * 
	 * <p>
	 * The <code>contentDir</code> needs to point to an existing directory in
	 * order to be indexed.
	 * </p>
	 */
	protected File contentDir;

	/** Description of collection. */
	protected String description;

	/** Indicates whether a collection actually esists on disk. */
	protected transient boolean existsOnDisk;

	/** id indicates identity. Used for persistency and presentation. */
	protected Long id;

	/**
	 * The indexDir is the directory where the index is stored.
	 * 
	 * <p>
	 * e.g. d:\temp\zilverline\index
	 * </p>
	 */
	protected File indexDir;

	/**
	 * The thread used to index this collection.
	 * 
	 */
	protected transient Thread indexingThread;

	/**
	 * Attribute used to find out whether <code>keepCache</code> has been set
	 * externally (using setter).
	 */
	protected transient boolean isKeepCacheSet;

	/**
	 * Indicated whether the cache should be removed after indexing.
	 * 
	 * <p>
	 * If not, search results can return files in for instance zip files.
	 * </p>
	 */
	protected boolean keepCache;

	/** The date of the last index of this collection. */
	protected transient Date lastIndexed;

	/** Reference back to the collectionManager. */
	protected transient CollectionManager manager;

	/** This cache is used to store the MD5 keys of all indexed documents. */
	protected transient Set md5DocumentCache;

	/** Name of collection, also used as part of the name of index. */
	protected String name;

	/**
	 * Variable used to possibly stop the indexing thread.
	 */
	protected transient boolean stopRequested;

	/**
	 * Number of Documents in this collection. Can only be set by actually
	 * consulting the corresponding index
	 */
	protected transient int numberOfDocs;

	/**
	 * The url is the location this collection is mapped to as a result of a
	 * search. e.g. d:\books\java could be mapped to https://server/path/java/
	 */
	protected String url;

	/** The version of the index of this collection. */
	protected transient long version;

	/**
	 * Returns an Analyzer for this collection based on configuration.
	 * 
	 * @return the Analyzer used to index and search this collection
	 * @todo the analyzer setting and creation is a bit funny, refactor some
	 *       time
	 * @see Analyzer
	 */
	public final Analyzer createAnalyzer() {
		if (analyzerObject != null) {
			return analyzerObject;
		} else {
			return manager.createAnalyzer();
		}
	}

	/**
	 * Determine whether the collection (contentDir) actually (now) exists on
	 * disk.
	 * 
	 * @return true if the collection exists
	 */
	public final boolean existsOnDisk() {
		setExistsOnDisk();
		return existsOnDisk;
	}

	/**
	 * Get the Analyzer.
	 * 
	 * @return the Analyzer as String
	 * @see org.apache.lucene.analysis.Analyzer
	 */
	public final String getAnalyzer() {
		return analyzer;
	}

	/**
	 * Gets the archive cache for this Collection.
	 * 
	 * <p>
	 * The archive cache is used to store the keys of archives that are
	 * extracted 'on-the-fly'
	 * </p>
	 * 
	 * @return HashSet containing archives that have been cached (so they have
	 *         been extracted)
	 */
	public final Set getArchiveCache() {
		return archiveCache;
	}

	/**
	 * Get the location where this collection's cache is kept on disk.
	 * 
	 * @return Returns the cacheDir.
	 */
	public final File getCacheDir() {
		return cacheDir;
	}

	/**
	 * Gets the directory where this collection's cache is stored. If the
	 * cacheDir is not set for this Collection, the name of this collection is
	 * used, possibly prepended with the (default) retrieved from the manager.
	 * The cache is used to (temporarily) store expanded content, such as zip
	 * files.
	 * 
	 * @return The directory where the cache of this collection is stored on
	 *         disk.
	 */
	public final File getCacheDirWithManagerDefaults() {
		if ((cacheDir == null) || "".equals(cacheDir.toString())) {
			if (manager != null) {
				// create a filename from the default index location and the
				// name of this collection
				return new File(manager.getCacheBaseDir(), name);
			} else {
				log.warn("Manager for " + name + " should not be null");
				return new File(name, "cache");
			}
		}

		return cacheDir;
	}

	/**
	 * Gets the URL where this collection's cached documents can be retrieved.
	 * 
	 * @return Returns the cacheUrl.
	 */
	public final String getCacheUrl() {
		return cacheUrl;
	}

	/**
	 * The URL maps the cacheDir to another location.
	 * 
	 * <p>
	 * e.g. A document 'ldap.pdf' in cacheDir 'e:\collection\cache\books\' with
	 * an cacheURL of 'http://search.company.com/cachedBooks/' will be returned
	 * in a search result as
	 * <code>http://search.company.com/cachedBooks/ldap.pdf</code>
	 * </p>
	 * 
	 * @return the cacheUrl of the collection, or the cacheDir as URL if url is
	 *         null or empty.
	 */
	public final String getCacheUrlWithManagerDefaults() {
		if (StringUtils.hasLength(cacheUrl)) {
			if (cacheUrl.endsWith("/")) {
				return cacheUrl;
			} else {
				return cacheUrl + "/";
			}
		} else {
			return "file://"
					+ getCacheDirWithManagerDefaults().toURI().getPath();
		}
	}

	/**
	 * Gets the location where this collection's documents can be retrieved.
	 * 
	 * @return contentDir directory of collection
	 */
	public final File getContentDir() {
		return contentDir;
	}

	/**
	 * Gets the origin from where this collection's documents can be retrieved.
	 * 
	 * @return location such as e:/docs or InBox
	 */
	public abstract String getRoot();

	/**
	 * Get the description of the collection.
	 * 
	 * @return description for the collection
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * Get the id of the collection.
	 * 
	 * @return unique id, can be null
	 */
	public final Long getId() {
		return id;
	}

	/**
	 * Get the location where this collection's index is kept on disk.
	 * 
	 * @return the indexDir, possibly null.
	 */
	public final File getIndexDir() {
		return indexDir;
	}

	/**
	 * 'Calculates' the directory where the index of this collection is stored
	 * on disk. If the indexDir is not set for this Collection, the name of this
	 * collection is used, possibly prepended with the baseDir retrieved from
	 * the manager.
	 * 
	 * @return The directory where the index of this collection is stored on
	 *         disk, never null
	 */
	public final File getIndexDirWithManagerDefaults() {
		if ((indexDir == null) || "".equals(indexDir.toString())) {
			if (manager != null) {
				// create a filename from the default index location and the
				// name of this collection
				return new File(manager.getIndexBaseDir(), name);
			} else {
				log.warn("Manager for " + name + " should not be null");
				return new File(name, "index");
			}
		}

		return indexDir;
	}

	/**
	 * Return the date of the last Index.
	 * 
	 * @return date of last Index, may return null
	 */
	public final Date getLastIndexed() {
		return lastIndexed;
	}

	/**
	 * Get the collection's manager.
	 * 
	 * @todo remove this dependency to service layer.
	 * 
	 * @return Reference to the CollectionManager holding this Collection.
	 */
	public final CollectionManager getManager() {
		return manager;
	}

	/**
	 * Gets the cache of MD5 hashes of all documents (previously) indexed.
	 * 
	 * @return HashSet containing hashes of all documents (previously) indexed
	 */
	public final Set getMd5DocumentCache() {
		return md5DocumentCache;
	}

	/**
	 * Get the name of this collection.
	 * 
	 * @return name of collection
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Get the number of documents in this collection. The number is not
	 * calculated, but stored after indexing process, so it is a cheap
	 * operation.
	 * 
	 * @return number of documents in collection
	 */
	public final int getNumberOfDocs() {
		if (isIndexingInProgress()) {
			IndexReader index = null;
			try {
				File thisIndex = getIndexDirWithManagerDefaults();
				index = IndexReader.open(thisIndex);

				if (index != null) {
					return index.numDocs();
				}
			} catch (IOException e) {
				log
						.warn("Error getting index for collection '" + name
								+ "'", e);
			} finally {
				if (index != null) {
					try {
						index.close();
					} catch (IOException e1) {
						log.error("Error closing index for collection " + name,
								e1);
					}
				}
			}
		}
		return numberOfDocs;
	}

	/**
	 * Gets the URL where this collection's documents can be retrieved.
	 * 
	 * @return the url
	 */
	public final String getUrl() {
		return url;
	}

	/**
	 * Determines the URL of the collection.
	 * <p>
	 * The URL maps the contentDir to another location. e.g. A document
	 * 'ldap.pdf' in contentDir 'e:\collection\books\' with an URL of
	 * 'http://search.company.com/books/' will be returned in a search result as
	 * <code>http://search.company.com/books/ldap.pdf</code>
	 * </p>
	 * 
	 * @return the URL of the collection as a String, possibly null in the
	 *         exeptional case where there is no contentDir
	 */
	public final String getUrlDefault() {
		if (StringUtils.hasLength(url)) {
			if (url.endsWith("/")) {
				return url;
			} else {
				return url + "/";
			}
		} else {
			if (contentDir != null) {
				return "file://" + contentDir.toURI().getPath();
			} else {
				log.warn("Collection " + name + " does not have a contentDir.");
				return null;
			}
		}
	}

	/**
	 * Return the version of the Index.
	 * 
	 * @return version of last Index
	 */
	public final long getVersion() {
		return version;
	}

	/**
	 * Index the given Collection.
	 * 
	 * @param fullIndex
	 *            indicated whether a full or incremental index should be
	 *            created
	 * @throws IndexException
	 *             if the Collections can not be indexed
	 */
	public abstract void index(final boolean fullIndex) throws IndexException;

	/**
	 * Index the given Collection in a background thread. Stops the indexing if
	 * already running.
	 * 
	 * @param fullIndex
	 *            indicated whether a full or incremental index should be
	 *            created
	 * @throws IndexException
	 *             if the Collections can not be indexed
	 */
	public final void indexInThread(final boolean fullIndex)
			throws IndexException {
		if (isIndexingInProgress()) {
			log.warn("Collection " + name
					+ " is already being indexed, now stopping");
			stopRequest();
			return;
		}
		stopRequested = false;
		Runnable r = new Runnable() {
			public void run() {
				try {
					index(fullIndex);
				} catch (Exception x) {
					// in case ANY exception slips through
					log
							.error(
									"Can't succesfully finish background indexing process",
									x);
				}
			}
		};
		if (fullIndex) {
			// update the info now so it is shown in user interface
			numberOfDocs = 0;
			lastIndexed = new Date();
		}
		indexingThread = new Thread(r);
		indexingThread.setName(name + "IndexingThread");
		if (manager.getPriority() != null) {
			indexingThread.setPriority(manager.getPriority().intValue());
		} else {
			indexingThread.setPriority(Thread.NORM_PRIORITY);
		}
		indexingThread.start();
	}

	/**
	 * Initialize this collection by getting its index. It retrieves the number
	 * of documents and the MD5 hash of all documents in the collection.
	 * 
	 * If the index does not exist (this is a new Collection) just return.
	 * 
	 * @throws IndexException
	 *             when existing index of Collection can not be succesfully
	 *             opened.
	 */
	public final void init() throws IndexException {
		log.debug("Initializing collection " + name);
		IndexReader index = null;
		// Determine whether the collection exists on disk
		setExistsOnDisk();
		// check whether this collection has a cache for the MD5 hashes of
		// documents
		if (md5DocumentCache == null) {
			md5DocumentCache = new HashSet();
		}
		// check whether this collection has a cache for the MD5 hashes of
		// indexed archives
		if (archiveCache == null) {
			archiveCache = new HashSet();
		}
		if (!isIndexValid()) {
			log.info("Index does not exist (yet) for collection '" + name
					+ "'. Possibly new collection.");
			numberOfDocs = 0;
			return;
		}

		// Get the index
		File thisIndex = getIndexDirWithManagerDefaults();
		try {
			index = IndexReader.open(thisIndex);

			if (index != null) {
				numberOfDocs = index.numDocs();
				// retrieve all hashes of Documents from the cache
				md5DocumentCache.clear();
				for (int i = 0; i < numberOfDocs; i++) {
					Document d = index.document(i);
					String hashValue = d.get("hash");
					md5DocumentCache.add(hashValue);
				}
				// get some relevant information from the index
				version = IndexReader.getCurrentVersion(thisIndex);
				// deprecated, but needed
				lastIndexed = new Date(IndexReader.lastModified(thisIndex));
				log.debug("Collection " + name + " has " + numberOfDocs
						+ " documents, index created at: " + lastIndexed);
			} else {
				log
						.error("Index could not be retrieved for collection "
								+ name);
			}
		} catch (IOException e) {
			throw new IndexException("Error initializing collection '" + name
					+ "'", e);
		} finally {
			if (index != null) {
				try {
					index.close();
				} catch (IOException e1) {
					log.error("Error closing index for collection " + name, e1);
				}
			} else {
				numberOfDocs = 0;
				version = 0;
				lastIndexed = null;
			}
		}
	}

	/**
	 * Returns whether the collection exists on disk. It does not actually
	 * determine that.
	 * 
	 * @return true if existsOnDisk
	 */
	public final boolean isExistsOnDisk() {
		return existsOnDisk;
	}

	/**
	 * Indicates whether any indexing is going on.
	 * 
	 * @return true if so.
	 */
	public final boolean isIndexingInProgress() {
		if (indexingThread == null) {
			return false;
		}
		return indexingThread.isAlive();
	}

	/**
	 * Check whether the index of this collection is valid. An index is valid
	 * when the directory exists and there is an index in it.
	 * 
	 * @return true if the index is valid, otherwise false.
	 * 
	 * @throws IndexException
	 *             when existing index of Collection can not be succesfully
	 *             opened.
	 */
	public final boolean isIndexValid() throws IndexException {
		File file = this.getIndexDirWithManagerDefaults();

		if ((file == null) || !IndexReader.indexExists(file)) {
			log.warn("Index '" + file + "' not valid for collection '" + name
					+ "'.");

			return false;
		}

		return true;
	}

	/**
	 * Returns whether the cache containing archive's contents should be kept
	 * after being indexed.
	 * 
	 * @return true if so.
	 */
	public final boolean isKeepCache() {
		return keepCache;
	}

	/**
	 * Determines whether the cache containing archive's contents should be kept
	 * after being indexed. It does so by retrieving the defaults from the
	 * manager if needed.
	 * 
	 * @return true if so.
	 */
	public final boolean isKeepCacheWithManagerDefaults() {
		if (isKeepCacheSet) {
			return keepCache;
		} else {
			if (manager != null) {
				// the value was not set here, get it from manager.
				return manager.isKeepCache();
			}
		}
		return false;
	}

	/**
	 * Indicates whether collection is just instantiated and has no id yet.
	 * 
	 * @return true if has no id yet
	 */
	public final boolean isNew() {
		return id == null;
	}

	/**
	 * Sets analyzer and creates an Analyzer object as specified by the given
	 * String.
	 * 
	 * @param analyzerClassName
	 *            the name of the class. The actual class needs to be available
	 *            on the classpath.
	 */
	public final void setAnalyzer(final String analyzerClassName) {
		try {
			if (StringUtils.hasText(analyzerClassName)) {
				analyzer = analyzerClassName;

				Class c = Class.forName(analyzerClassName);

				if (c != null) {
					log.debug("Returning Analyzer: '" + analyzerClassName + "'");
					analyzerObject = (Analyzer) c.newInstance();
				}
			}
		} catch (InstantiationException e1) {
			log.warn("Can not initiate Analyzer '" + analyzerClassName, e1);
		} catch (IllegalAccessException e1) {
			log.warn("Can not access Analyzer " + analyzerClassName, e1);
		} catch (ClassNotFoundException e) {
			log.warn("Class not found: " + analyzerClassName, e);
		}
	}

	/**
	 * Sets the cacheDir of the collection.
	 * 
	 * The cache is used to (temporarily) store expanded content, such as zip
	 * files.
	 * 
	 * @param thisCacheDir
	 *            The directory where the cache of this collection is stored on
	 *            disk.
	 */
	public final void setCacheDir(final File thisCacheDir) {
		cacheDir = thisCacheDir;
	}

	/**
	 * Sete the cacheUrl of the collection.
	 * 
	 * The URL is the prefix that corresponds to the cacheDir. e.g. A document
	 * 'ldap.pdf' in cacheDir 'd:\temp\books\cache\' can be returned in a search
	 * result as http://search.company.com/books/cache/ldap.pdf The URL in this
	 * case is the prefix 'http://search.company.com/books/cache/'
	 * 
	 * @param theCacheURL
	 *            the URL of this collection's cache.
	 */
	public final void setCacheUrl(final String theCacheURL) {
		cacheUrl = theCacheURL;
	}

	/**
	 * Sets the content directory of the collection, and checks whether the
	 * contentDir actually exists by setting existsOnDisk.
	 * 
	 * @param theContentDir
	 *            directory where collection sits on disk.
	 */
	public final void setContentDir(final File theContentDir) {
		contentDir = theContentDir;

		// check whether contentDir is really a directory
		setExistsOnDisk();

		if (!isExistsOnDisk()) {
			log.warn("Set contentDir for collection '" + name
					+ "' at non-existing: " + contentDir);
		} else {
			log.info("Set contentDir for collection '" + name + "' at: "
					+ contentDir);
		}
	}

	/**
	 * Set the description of the collection.
	 * 
	 * @param theDescription
	 *            description for collection
	 */
	public final void setDescription(final String theDescription) {
		description = theDescription;
	}

	/**
	 * Sets existsOnDisk based on whether the collection (contentDir) actually
	 * (now) sits on disk.
	 * 
	 * @todo the whole existsOnDisk construction is a little funny, refactor
	 *       some time
	 */
	protected abstract void setExistsOnDisk();

	/**
	 * Set the id of the collection. The id is used by the collectionManager to
	 * add and retrieve collections.
	 * 
	 * @param theId
	 *            the Id
	 */
	public final void setId(final Long theId) {
		id = theId;
	}

	/**
	 * Set the indexDir of this collection. The indexDir is the directory where
	 * the index is stored.
	 * 
	 * <p>
	 * e.g. d:\temp\zilverline\index
	 * </p>
	 * 
	 * @param theIndexDir
	 *            path to the index
	 */
	public final void setIndexDir(final File theIndexDir) {
		indexDir = theIndexDir;
	}

	/**
	 * Set whether the cache should be deleted after indexing this collection.
	 * In the meantime mark this attribute as being set externally, to
	 * distinguish from the default value of a boolean.
	 * 
	 * @param b
	 *            true or false
	 */
	public final void setKeepCache(final boolean b) {
		keepCache = b;
		isKeepCacheSet = true;
	}

	/**
	 * Set the collectionManager.
	 * 
	 * @param thisManager
	 *            The CollectionManager holding this collection.
	 */
	public final void setManager(final CollectionManager thisManager) {
		this.manager = thisManager;
	}

	/**
	 * Set the name of the Collection.
	 * 
	 * @param theName
	 *            name for collection
	 */
	public final void setName(final String theName) {
		name = theName;
	}

	/**
	 * Set the URL of this Collection. The URL is the prefix that corresponds to
	 * the contentDir. e.g. A document 'ldap.pdf' in contentDir
	 * 'e:\collection\books\' can be returned in a search result as
	 * http://search.company.com/books/ldap.pdf The URL in this case is the
	 * prefix 'http://search.company.com/books/'
	 * 
	 * @param theURL
	 *            the URL of this collection.
	 */
	public final void setUrl(final String theURL) {
		url = theURL;
	}

	/**
	 * Stop the indexing thread.
	 */
	public final void stopRequest() {
		stopRequested = true;
		indexingThread.interrupt();
	}

	/**
	 * Resets the cache by deleting all keys, and removing documents form cache.
	 * 
	 * @param fullIndex
	 * @throws IndexException
	 */
	protected void resetCache(final boolean fullIndex) throws IndexException {
		// create a cache for MD5 hashes if there's not one yet (new collection,
		// first indexed)
		if (this.getMd5DocumentCache() == null) {
			this.init();
		}
		if (fullIndex) {
			this.getMd5DocumentCache().clear();

			// make sure the cache is flushed
			if ((this.getCacheDirWithManagerDefaults() != null)
					&& this.getCacheDirWithManagerDefaults().exists()) {
				log.debug("Removing cache of " + this.getName());

				boolean success = FileUtils.removeDir(this
						.getCacheDirWithManagerDefaults());

				if (!success) {
					log
							.warn("Could not entirely delete cache prior to indexing '"
									+ this.getContentDir()
									+ "'. \n\tThis may result in an inconsistent index, as some cache may not be inaccessible, "
									+ "\n\tor old parts of cache included in new index.");
				}
			}
		}
	}

	/**
	 * Collections are equal if their id are.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof AbstractCollection) {
			AbstractCollection thatCollection = ((AbstractCollection) obj);
			if (this.getId() == null) {
				return thatCollection.getId() == null;
			}
			return thatCollection.getId().equals(this.getId());
		}
		return false;
	}

	public int hashCode() {

		return this.getId().hashCode();
	}

}
