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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.springframework.util.StringUtils;
import org.zilverline.core.DocumentCollection;
import org.zilverline.core.ExtractorFactory;
import org.zilverline.core.FileSystemCollection;
import org.zilverline.core.Handler;
import org.zilverline.core.IndexException;
import org.zilverline.dao.CollectionManagerDAO;
import org.zilverline.dao.DAOException;
import org.zilverline.util.FileUtils;
import org.zilverline.util.SysUtils;

/**
 * The CollectionManagerImpl holds all collections, and base values for them.
 * 
 * <p>
 * NB. This Bean gets instantiated in web-servlet.xml.
 * </p>
 * 
 * @author Michael Franken
 * @version $Revision: 1.28 $
 * 
 * @see org.zilverline.core.FileSystemCollection
 */
public class CollectionManagerImpl implements CollectionManager {

    /**
     * @return Returns the analyzer.
     */
    public String getAnalyzer() {
        return analyzer;
    }

    /**
     * Gets a collection by id.
     * 
     * @param theId The id of the collection
     * 
     * @return Collection or null if not found
     */
    public DocumentCollection getCollection(final Long theId) {
        Iterator li = collections.iterator();
        while (li.hasNext()) {
            DocumentCollection c = (DocumentCollection) li.next();
            if (theId.equals(c.getId())) {
                return c;
            }
        }
        return null;
    }

    /**
     * DAO object taking care of persistence for CollectionManager and its collections.
     */
    private transient CollectionManagerDAO dao;

    /**
     * MergeFactor for indexing process.
     */
    private Integer mergeFactor;

    /**
     * minMergeDocs for indexing process.
     */
    private Integer minMergeDocs;

    /**
     * maxMergeDocs for indexing process.
     */
    private Integer maxMergeDocs;

    /**
     * priority for indexing process.
     */
    private Integer priority = new Integer(2);

    /**
     * @return Returns the dao.
     */
    public CollectionManagerDAO getDao() {
        return dao;
    }

    /**
     * Set the DAO for this CollectionManager.
     * 
     * @param thisDao The dao to set.
     */
    public void setDao(final CollectionManagerDAO thisDao) {
        this.dao = thisDao;
    }

    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(CollectionManagerImpl.class);

    /**
     * String representation of Analyzer. Stored to present to user for selection.
     */
    private String analyzer = "org.apache.lucene.analysis.standard.StandardAnalyzer";

    /** Array containing all available Analyzers. */
    private transient String[] allAnalyzers;

    /**
     * The Analyzer to be used in indexing and searching. StandardAnalyzer by default.
     */
    private transient Analyzer analyzerObject = new StandardAnalyzer();

    /**
     * The default cache base directory for all collections. The cache is the directory on disk where zipped content is unzipped for
     * indexing. By default use WEB-INF/cache
     */
    private File cacheBaseDir = new File(new File(this.getClass().getResource("/").getFile()).getParentFile(), "cache");

    /** The set of collections this CollectionManagerImpl manages. */
    // TODO refactor this into a Set
    private List collections = new ArrayList();

    /**
     * The default index base directory for all collections. The index is the directory on disk where a Lucene index is stored. By
     * default use WEB-INF/index
     * 
     * @see org.apache.lucene.index.IndexReader
     */
    private File indexBaseDir = new File(new File(this.getClass().getResource("/").getFile()).getParentFile(), "index");

    /** The default for all collections whether to keep cache dir after indexing. */
    private boolean keepCache = false;

    /**
     * The handler of archives, contains mappings for file extension to unarchiving programs.
     */
    private Handler archiveHandler;

    /**
     * Factory containing Extractor mapppings.
     */
    private ExtractorFactory factory = new ExtractorFactory();

    /**
     * Array containing all Extractors by name.
     */
    private transient String[] allExtractors;

    /**
     * Add or updates collection to list of collections, and sets the manager.
     * 
     * Update occurs when a collection with the same id is found in the collections. Assigns an ID to a new Collection.
     * 
     * @param col Collection containing documents
     */
    public void addCollection(final DocumentCollection col) {
        long maxId = 0L;
        // TODO: Not so efficient and clean
        if (col.getId() != null) {
            // try to find previous occurence in list
            for (int i = 0; i < collections.size(); i++) {
                DocumentCollection thisCollection = (DocumentCollection) collections.get(i);
                maxId = Math.max(maxId, thisCollection.getId().longValue());
                log.debug("max ID: " + maxId);
                if (col.getId().equals(thisCollection.getId())) {
                    log.debug("Updating collection " + col.getId() + ", " + col.getName() + " to Manager at location " + i);
                    collections.set(i, col);
                    thisCollection.setManager(this);
                    return;
                }
            }
        } else {
            for (int i = 0; i < collections.size(); i++) {
                DocumentCollection thisCollection = (DocumentCollection) collections.get(i);
                maxId = Math.max(maxId, thisCollection.getId().longValue());
                log.debug("max ID: " + maxId);
            }
        }
        // it must be a new, or non existing collection, add it
        col.setId(new Long(maxId + 1L));
        log.debug("Adding collection " + col.getId() + ", " + col.getName() + " to Manager at end");
        collections.add(col);
        col.setManager(this);
    }

    /**
     * Deletes collection from list of collections.
     * 
     * @param col Collection containing documents
     */
    public void deleteCollection(final DocumentCollection col) {
        for (int i = 0; i < collections.size(); i++) {
            DocumentCollection thisCollection = (DocumentCollection) collections.get(i);
            if (col.getId().equals(thisCollection.getId())) {
                collections.remove(i);
                break;
            }
        }
    }

    /**
     * Indicates whether any indexing is going on.
     * 
     * @return true if so.
     */
    public boolean isIndexingInProgress() {
        for (int i = 0; i < collections.size(); i++) {
            DocumentCollection thisCollection = (DocumentCollection) collections.get(i);
            if (thisCollection.isIndexingInProgress()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Store the CollectionManager to store.
     * 
     * @throws IndexException when collectionManager can not be saved to underlying store
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
            log.error("No DAO set for IndexService");
        }
    }

    /**
     * Returns an Analyzer for this collection based on configuration.
     * 
     * @return the Analyzer used to index and search this collection
     */

    // TODO: rework this, this should actually be getAnalyzer, but that doesn't
    // work because of hack above.
    public Analyzer createAnalyzer() {
        return analyzerObject;
    }

    /**
     * Get the cache base directory.
     * 
     * @return String the directory where the cache sits
     */
    public File getCacheBaseDir() {
        return cacheBaseDir;
    }

    /**
     * Gets a collection by name.
     * 
     * @param theName The name of the collection
     * 
     * @return Collection or null if not found
     */
    public DocumentCollection getCollectionByName(final String theName) {
        if (theName == null) {
            return null;
        }

        Iterator li = collections.iterator();

        while (li.hasNext()) {
            DocumentCollection c = (DocumentCollection) li.next();

            if (theName.equals(c.getName())) {
                return c;
            }
        }

        return null;
    }

    /**
     * Get all collections.
     * 
     * @return collections List of collections
     */
    public List getCollections() {
        return collections;
    }

    /**
     * Get the base directory for the index.
     * 
     * @return the directory
     */
    public File getIndexBaseDir() {
        return indexBaseDir;
    }

    /**
     * Initializes all collections.
     * 
     * @throws IndexException if one of the collections can not be initialized.
     */
    public void init() throws IndexException {
        allExtractors = ExtractorFactory.findExtractorsOnClasspath();
        allAnalyzers = Handler.findAnalyzersOnClasspath();

        CollectionManager thatManager = dao.load();
        if (thatManager != null) {
            this.cacheBaseDir = thatManager.getCacheBaseDir();
            this.indexBaseDir = thatManager.getIndexBaseDir();
            this.keepCache = thatManager.isKeepCache();
            this.analyzer = thatManager.getAnalyzer();
            this.archiveHandler = thatManager.getArchiveHandler();
            this.factory = thatManager.getFactory();
            this.priority = thatManager.getPriority();
            this.mergeFactor = thatManager.getMergeFactor();
            this.maxMergeDocs = thatManager.getMaxMergeDocs();
            this.minMergeDocs = thatManager.getMinMergeDocs();
            // if there is nothing, probably first time Zilverline runs

            collections.clear();
            Iterator li = thatManager.getCollections().iterator();
            try {
                while (li.hasNext()) {
                    DocumentCollection c = (DocumentCollection) li.next();
                    log.debug("Adding collection to manager: " + c.getName());
                    this.addCollection(c);
                    c.init();
                }
            }
            catch (IndexException e) {
                throw new IndexException("Error initializing all indexes in CollectionManagerImpl", e);
            }
        } else {
            // possibly first time Zilverline runs
            setFactory(new ExtractorFactory());
            setArchiveHandler(new Handler());

            if (getCollections().isEmpty()) {
                // try {
                // // make an initial Collection
                // Properties props = System.getProperties();
                // log.debug(props.toString());
                // String profile = System.getProperty("user.home");
                // if (profile != null) {
                // // probably on Windows
                // File myDocs = new File(profile);
                // if (myDocs.isDirectory()) {
                // log.info("Welcome to Zilverline. Creating initial collection at: " + myDocs);
                // // create a Collection
                // FileSystemCollection myCollection = new FileSystemCollection();
                // myCollection.setName("My Documents");
                // myCollection.setContentDir(myDocs);
                // addCollection(myCollection);
                // myCollection.init();
                // myCollection.indexInThread(true);
                // } else {
                // log.debug("Can't find " + myDocs + ", skip creating default collection");
                // }
                // } else {
                // log.debug("Can't find profile, skip creating default collection");
                // }
                //
                // } catch (IndexException e) {
                // log.warn("Can't initialize, skip creating default collection", e);
                // }
            }
        }
    }

    /**
     * The default for all collections whether to keep cache dir after indexing.
     * 
     * @return whether to keep the cache or not.
     */
    public boolean isKeepCache() {
        return keepCache;
    }

    /**
     * Create an Analyzer as specified by the given String.
     * 
     * @param analyzerClassName the name of the class. The class needs to be available on the classpath.
     */
    public void setAnalyzer(final String analyzerClassName) {
        try {
            if (analyzerClassName != null) {
                analyzer = analyzerClassName;

                Class c = Class.forName(analyzerClassName);

                if (c != null) {
                    log.debug("Returning Analyzer: " + analyzerClassName);
                    analyzerObject = (Analyzer) c.newInstance();
                }
            }
        }
        catch (InstantiationException e1) {
            log.debug("Can not initiate Analyzer '" + analyzerClassName, e1);
        }
        catch (IllegalAccessException e1) {
            log.debug("Can not access Analyzer " + analyzerClassName, e1);
        }
        catch (ClassNotFoundException e) {
            log.debug("Class not found: " + analyzerClassName, e);
        }
    }

    /**
     * The default cache base directory for all collections. The cache is the directory on disk where zipped content is unzipped for
     * indexing.
     * 
     * @param thisDir the directory on disk
     */
    public void setCacheBaseDir(final File thisDir) {
        cacheBaseDir = thisDir;
    }

    /**
     * The default index base directory for all collections. The index is the directory on disk where a Lucene index is stored.
     * 
     * @param thisDir the directory on disk
     * 
     * @see org.apache.lucene.index.IndexReader
     */
    public void setIndexBaseDir(final File thisDir) {
        indexBaseDir = thisDir;
    }

    /**
     * Indicates whether a Collection cache should be kept after indexing. The value of this CollectionManagerImpl functions as
     * default for all Collections.
     * 
     * @param b keep cache or not.
     */
    public void setKeepCache(final boolean b) {
        keepCache = b;
    }

    /**
     * @return Returns the allAnalyzers.
     */
    public String[] getAllAnalyzers() {
        return allAnalyzers;
    }

    /**
     * get the ArchiveHandler, which contains the mappings for unArchiving archives.
     * 
     * @return object containing mappings for handling archives
     */
    public Handler getArchiveHandler() {
        return archiveHandler;
    }

    /**
     * @return Returns the factory.
     */
    public ExtractorFactory getFactory() {
        return factory;
    }

    /**
     * Set the ArchiveHandler.
     * 
     * @param handler object containing mappings for handling archives
     */
    public void setArchiveHandler(final Handler handler) {
        archiveHandler = handler;
    }

    /**
     * @param thatFactory The factory to set.
     */
    public void setFactory(final ExtractorFactory thatFactory) {
        this.factory = thatFactory;
    }

    /**
     * Expands Archives to disk. This is used is 'on-the-fly' extraction from cache
     * 
     * @param col the Collection to which cache this archive is extracted
     * @param zip the archive or directory that might contain archives
     * 
     * @return true if archive(s) could be extracted
     * 
     * @throws IndexException on error
     * 
     * @see org.zilverline.web.CacheController
     */
    public boolean expandArchive(final FileSystemCollection col, final File zip) throws IndexException {
        log.debug("getFromCache: document " + zip + " from : " + col.getName());

        if (!zip.exists()) {
            log.warn(zip + " does not exist.");

            return false;
        }

        // in the recursion we could have a directory
        if (zip.isDirectory()) {
            File[] files = zip.listFiles();

            for (int i = 0; i < files.length; i++) {
                expandArchive(col, files[i]);
            }
        } else {
            String extension = FileUtils.getExtension(zip);

            if ((archiveHandler != null) && archiveHandler.canUnPack(extension)) {
                // we have an archive
                log.debug(zip + " is an archive");

                File dir = null;

                if (StringUtils.hasText(archiveHandler.getUnArchiveCommand(extension))) {
                    // this is a zip: handle with java's zip capabilities
                    log.debug(zip + " is a zip file");
                    dir = unZip(zip, col);
                } else {
                    log.debug(zip + " is a external archive file");
                    dir = unPack(zip, col);
                }

                // recursively handle all archives in this one
                log.debug("Recurse into " + dir);
                File[] files = dir.listFiles();

                for (int i = 0; i < files.length; i++) {
                    expandArchive(col, files[i]);
                }

                return true;
            } else {
                if (archiveHandler == null || archiveHandler.getMappings() == null) {
                    log.warn("Can't extract this type, no archiveHandler");
                } else {
                    log.warn("Can't extract this type, not a supported extension: " + extension);
                }
            }
        }

        return false;
    }

    /**
     * 'unpacks' a given archive file into cache directory with derived name. e.g. c:\temp\file.chm wil be unpacked into
     * [cacheDir]\file_chm\.
     * 
     * @param sourceFile the Archive file to be unpacked
     * @param thisCollection the collection whose cache and contenDir is used
     * 
     * @return File (new) directory containing unpacked file, null if unknown Archive
     */
    public File unPack(final File sourceFile, final FileSystemCollection thisCollection) {
        File unPackDestinationDirectory = null;
        // based on file extension, lookup in the archiveHandler whether this is a known archive
        // we don't really have to do this, since it is already been checked in the calling indexDocs(), but better safe then sorry

        if (archiveHandler == null) {
            // we have an unknown archive
            log.warn("No archiveHandler found while trying to unPack " + sourceFile);
            return null;
        }

        String extension = FileUtils.getExtension(sourceFile);

        if (!archiveHandler.canUnPack(extension)) {
            // we have an unknown archive
            log.warn("No archiveHandler found for " + sourceFile);
            return null;
        }

        // Create destination where file will be unpacked
        unPackDestinationDirectory = file2CacheDir(sourceFile, thisCollection);
        log.debug("unpacking " + sourceFile + " into " + unPackDestinationDirectory);

        // get the command from the map by supplying the file extension
        String unArchiveCommand = archiveHandler.getUnArchiveCommand(extension);

        if (SysUtils.execute(unArchiveCommand, sourceFile, unPackDestinationDirectory)) {
            log.info("Executed: " + unArchiveCommand + " " + sourceFile + " in " + unPackDestinationDirectory);
        } else {
            log.warn("Can not execute " + unArchiveCommand + " " + sourceFile + " in " + unPackDestinationDirectory);
        }
        // delete the archive file if it is in the cache, we don't need to
        // store it, since we've extracted the contents
        if (FileUtils.isIn(sourceFile, thisCollection.getCacheDirWithManagerDefaults())) {
            sourceFile.delete();
        }

        return unPackDestinationDirectory;
    }

    /**
     * Takes a file and creates a directory with a derived name in the cacheDir. If the file was not already in cache, and sits in
     * contentDir it is mapped to cache, otherwise it stays within cache.
     * 
     * <p>
     * e.g. given cachedir <code>c:\temp\</code> and contentdir <code>e:\docs\Projects\lucene\content\</code>,
     * <code>e:\docs\Projects\lucene\content\books.zip</code> yields <code>c:\temp\books_zip\</code>
     * </p>
     * 
     * <p>
     * <code>c:\temp\books.zip</code> yields <code>c:\temp\books_zip\</code>
     * </p>
     * 
     * @param sourceFile the file to be used as name for the directory
     * @param thisCollection the collection (not null) whose cache and contenDir is used
     * 
     * @return File the (newly created) directory
     */
    public static File file2CacheDir(final File sourceFile, final FileSystemCollection thisCollection) {
        log.debug("Entering file2Dir, with " + sourceFile + ", for collection:" + thisCollection.getName());

        File unZipDestinationDirectory = null;

        try {
            File cacheDir = thisCollection.getCacheDirWithManagerDefaults();
            // just to be sure the cacheDir exists
            if (!cacheDir.isDirectory()) {
                if (!cacheDir.mkdirs()) {
                    log.warn("Can't create cache directory " + cacheDir);
                    return null;
                }
            }
            // get the full path (not just the name, since we could have recursed into newly created directory
            String destinationDirectory = sourceFile.getCanonicalPath();

            // change extension into _
            int index = destinationDirectory.lastIndexOf('.');
            String extension;

            if (index != -1) {
                extension = destinationDirectory.substring(index + 1);
                destinationDirectory = destinationDirectory.substring(0, index) + '_' + extension;
            }

            // if sourceFile still sits in contentdir it must be mapped to cache
            String collectionPath = thisCollection.getContentDir().getCanonicalPath();

            if (destinationDirectory.startsWith(collectionPath)) {
                // chop off the first part (collectionPath)
                String relativePath = destinationDirectory.substring(collectionPath.length());

                unZipDestinationDirectory = new File(thisCollection.getCacheDirWithManagerDefaults(), relativePath);
                log.debug("Mapped " + relativePath + " to cache: " + thisCollection.getCacheDirWithManagerDefaults());
            } else {
                unZipDestinationDirectory = new File(destinationDirectory);
            }

            // actually create the directory
            boolean canCreate = unZipDestinationDirectory.mkdirs();

            if (!canCreate) {
                log.warn("Could not create: " + unZipDestinationDirectory);
            }

            log.debug("Created: " + unZipDestinationDirectory + " from File: " + sourceFile);
        }
        catch (Exception e) {
            log.error("error creating directory from file: " + sourceFile, e);
        }

        return unZipDestinationDirectory;
    }

    /**
     * unZips a given zip file into cache directory with derived name. e.g. c:\temp\file.zip wil be unziiped into
     * [cacheDir]\file_zip\.
     * 
     * @param sourceZipFile the ZIP file to be unzipped
     * @param thisCollection the collection whose cache and contenDir is used
     * 
     * @return File (new) directory containing zip file
     */
    public static File unZip(final File sourceZipFile, final FileSystemCollection thisCollection) {
        // specify buffer size for extraction
        final int aBUFFER = 2048;
        File unzipDestinationDirectory = null;
        ZipFile zipFile = null;
        FileOutputStream fos = null;
        BufferedOutputStream dest = null;
        BufferedInputStream bis = null;

        try {
            // Specify destination where file will be unzipped
            unzipDestinationDirectory = file2CacheDir(sourceZipFile, thisCollection);
            log.info("unzipping " + sourceZipFile + " into " + unzipDestinationDirectory);
            // Open Zip file for reading
            zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
            // Create an enumeration of the entries in the zip file
            Enumeration zipFileEntries = zipFile.entries();
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                log.debug("Extracting: " + entry);
                File destFile = new File(unzipDestinationDirectory, currentEntry);
                // grab file's parent directory structure
                File destinationParent = destFile.getParentFile();
                // create the parent directory structure if needed
                destinationParent.mkdirs();
                // extract file if not a directory
                if (!entry.isDirectory()) {
                    bis = new BufferedInputStream(zipFile.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte[] data = new byte[aBUFFER];
                    // write the current file to disk
                    fos = new FileOutputStream(destFile);
                    dest = new BufferedOutputStream(fos, aBUFFER);
                    // read and write until last byte is encountered
                    while ((currentByte = bis.read(data, 0, aBUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    bis.close();
                }
            }
            zipFile.close();
            // delete the zip file if it is in the cache, we don't need to store
            // it, since we've extracted the contents
            if (FileUtils.isIn(sourceZipFile, thisCollection.getCacheDirWithManagerDefaults())) {
                sourceZipFile.delete();
            }
        }
        catch (Exception e) {
            log.error("Can't unzip: " + sourceZipFile, e);
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (dest != null) {
                    dest.close();
                }
                if (bis != null) {
                    bis.close();
                }
            }
            catch (IOException e1) {
                log.error("Error closing files", e1);
            }
        }

        return unzipDestinationDirectory;
    }

    /**
     * @return Returns the allExtractors.
     */
    public String[] getAllExtractors() {
        return allExtractors;
    }

    /**
     * @return Returns the mergeFactor.
     */
    public Integer getMergeFactor() {
        return mergeFactor;
    }

    /**
     * @param mergeFactor The mergeFactor to set.
     */
    public void setMergeFactor(Integer mergeFactor) {
        this.mergeFactor = mergeFactor;
    }

    /**
     * @return Returns the priority.
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority The priority to set.
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return Returns the maxMergeDocs.
     */
    public Integer getMaxMergeDocs() {
        return maxMergeDocs;
    }

    /**
     * @param maxMergeDocs The maxMergeDocs to set.
     */
    public void setMaxMergeDocs(Integer maxMergeDocs) {
        this.maxMergeDocs = maxMergeDocs;
    }

    /**
     * @return Returns the minMergeDocs.
     */
    public Integer getMinMergeDocs() {
        return minMergeDocs;
    }

    /**
     * @param minMergeDocs The minMergeDocs to set.
     */
    public void setMinMergeDocs(Integer minMergeDocs) {
        this.minMergeDocs = minMergeDocs;
    }
}
