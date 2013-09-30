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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import org.springframework.util.StringUtils;
import org.zilverline.service.CollectionManagerImpl;
import org.zilverline.util.FileUtils;
import org.zilverline.util.StopWatch;

/**
 * A Collection is a number of documents in a directory that are indexed together.
 * 
 * @author Michael Franken
 * @version $Revision: 1.19 $
 */
public class FileSystemCollection extends AbstractCollection {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(FileSystemCollection.class);

    /**
     * Default Constructor setting all fields to non null defaults.
     */
    public FileSystemCollection() {
        name = "";
        url = "";
        description = "";
        numberOfDocs = 0;
        version = 0;
        lastIndexed = null;
        existsOnDisk = false;
        keepCache = false;
        isKeepCacheSet = false;
        // other constructor stuff should appear here first ...
        log.debug("in constructor - initializing...");
    }

    /**
     * Sets existsOnDisk based on whether the collection (contentDir) actually (now) sits on disk.
     * 
     * @todo the whole existsOnDisk construction is a little funny, refactor some time
     */
    protected void setExistsOnDisk() {
        if (contentDir == null) {
            existsOnDisk = false;
        } else {
            existsOnDisk = contentDir.isDirectory();
        }
    }

    /**
     * Gets the origin from where this collection's documents can be retrieved.
     * 
     * @return location such as e:/docs or InBox
     */
    public final String getRoot() {
        if (getContentDir() == null) {
            return "-";
        }
        return getContentDir().getAbsolutePath();
    }

    /**
     * Prints Collection as String for logging.
     * 
     * @return pretty formatted information about the collection
     */
    public final String toString() {
        return "Collection(" + id + "), with name: " + name + ",\n\t\tdescription: " + description + ",\n\t\tcontentDir: "
            + contentDir + ",\n\t\turl: " + url + ",\n\t\texistsOnDisk: " + existsOnDisk + ",\n\t\tindexDir: " + indexDir
            + ",\n\t\tcacheDir: " + cacheDir + ",\n\t\tcacheUrl: " + cacheUrl + ",\n\t\tanalyzer: " + analyzer
            + ",\n\t\tkeepCache: " + keepCache + ",\n\t\tisKeepCacheSet: " + isKeepCacheSet + ",\n\t\tnumberOfDocs: "
            + numberOfDocs + ",\n\t\tmanager: " + manager + ",\n\t\tlastIndexed: " + lastIndexed;
        // +
        // ",\n\t\tmd5DocumentCache:
        // " + md5DocumentCache +
        // "\n\n";
    }

    /**
     * Index the given Collection.
     * 
     * @param fullIndex indicated whether a full or incremental index should be created
     * @throws IndexException if the Collections can not be indexed
     */
    public final void index(final boolean fullIndex) throws IndexException {
        log.info("Starting creation of index of " + this.getContentDir());

        IndexWriter writer = null;

        try {
            // record start time
            StopWatch watch = new StopWatch();

            watch.start();

            // make sure the index exists
            File indexDirectory = this.getIndexDirWithManagerDefaults();

            // reindex if the index is not there or invalid
            int currentNumberOfDocs = 0;
            boolean mustReindex = fullIndex;
            if (!this.isIndexValid()) {
                mustReindex = true;
                indexDirectory.mkdirs();
            } else {
                currentNumberOfDocs = getNumberOfDocs();
            }

            // create an index(writer)
            writer = new IndexWriter(indexDirectory, this.createAnalyzer(), mustReindex);
            // see whether there are specific indexing settings in manager
            if (manager.getMergeFactor() != null) {
                writer.setMergeFactor(manager.getMergeFactor().intValue());
            }
            if (manager.getMinMergeDocs() != null) {
                writer.setMaxBufferedDocs(manager.getMinMergeDocs().intValue());
            }

            if (manager.getMaxMergeDocs() != null) {
                writer.setMaxMergeDocs(manager.getMaxMergeDocs().intValue());
            }

            resetCache(fullIndex);

            // prepare Index parameters
            IndexCommand ic = new IndexCommand();

            ic.setWriter(writer);
            ic.setCollection(this);
            ic.setFile(this.getContentDir());
            ic.setInZip(false);
            ic.setStart(true);

            // and start indexing
            this.indexDocs(ic);
            log.debug("Optimizing index of " + this.getContentDir());
            writer.optimize();

            // update the info of this collection
            this.init();

            // record end time and report duration of indexing
            watch.stop();
            log.info("Indexed " + (writer.docCount() - currentNumberOfDocs) + " new documents in " + watch.elapsedTime());
        }
        catch (IOException e) {
            throw new IndexException("Error indexing '" + this.getName() + "'. Possibly unable to remove old index", e);
        }
        catch (Exception e) {
            throw new IndexException("Error indexing '" + this.getName() + "'", e);
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e1) {
                    // assume the index is made, just can't close, so don't
                    // rethrow, just log
                    log.error("Error closing index for " + this.getName(), e1);
                }
            }
        }

    }

    /**
     * Index the given Collection.
     * 
     * @param fullIndex indicated whether a full or incremental index should be created
     * @throws IndexException if the Collections can not be indexed
     */
    // TODO: this really looks like the previous method: refactor!
    public final void indexFile(final File theFile) throws IndexException {
        log.info("Adding File " + theFile + " to collection " + name);

        IndexWriter writer = null;

        try {
            // record start time
            StopWatch watch = new StopWatch();

            watch.start();

            // make sure the index exists
            File indexDirectory = this.getIndexDirWithManagerDefaults();

            int currentNumberOfDocs = getNumberOfDocs();

            boolean reindex = false;
            if (!isIndexValid()) {
                log.debug("Index for " + name + " is not valid, create a new one");
                reindex = true;
            }

            // create an index(writer)
            writer = new IndexWriter(indexDirectory, this.createAnalyzer(), reindex);
            // see whether there are specific indexing settings in manager
            if (manager.getMergeFactor() != null) {
                writer.setMergeFactor(manager.getMergeFactor().intValue());
            }
            if (manager.getMinMergeDocs() != null) {
                writer.setMaxBufferedDocs(manager.getMinMergeDocs().intValue());
            }

            if (manager.getMaxMergeDocs() != null) {
                writer.setMaxMergeDocs(manager.getMaxMergeDocs().intValue());
            }

            // prepare Index parameters
            IndexCommand ic = new IndexCommand();

            ic.setWriter(writer);
            ic.setCollection(this);
            ic.setFile(theFile);
            ic.setInZip(false);
            ic.setStart(true);

            // and start indexing
            this.indexDocs(ic);
            log.debug("Optimizing index of " + this.getContentDir());
            writer.optimize();

            // update the info of this collection
            this.init();

            // record end time and report duration of indexing
            watch.stop();
            log.info("Indexed " + (writer.docCount() - currentNumberOfDocs) + " new documents in " + watch.elapsedTime());
        }
        catch (IOException e) {
            throw new IndexException("Error indexing '" + this.getName() + "'. Possibly unable to remove old index", e);
        }
        catch (Exception e) {
            throw new IndexException("Error indexing '" + this.getName() + "'", e);
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e1) {
                    // assume the index is made, just can't close, so don't
                    // rethrow, just log
                    log.error("Error closing index for " + this.getName(), e1);
                }
            }
        }

    }

    /**
     * Reads a File from IndexCommand (a directory, 'straight' file or an archive) and creates an index for all files recursively.
     * 
     * <p>
     * now supports pdf, rtf, html, txt, rar, zip, chm and doc formats.
     * </p>
     * 
     * @param ic IndexCommand
     * 
     * @throws IndexException when Indexing stops
     */
    private void indexDocs(final IndexCommand ic) throws IndexException {
        if (stopRequested) {
            log.info("Indexing stops, due to request");
            return;
        }
        log.debug("indexDocs: document #" + ic.getWriter().docCount() + ": " + ic);
        if (ic.getFile().isDirectory()) {
            if (!FileUtils.isLink(ic.getFile())) {
                indexDirectory(ic);
            } else {
                log.warn("Skipping symbolic link: " + ic.getFile().getAbsolutePath());
            }
        } else {
            // handle composed docs first based on file extension, lookup in the manager.getArchiveHandler() whether
            // this is an archive
            // TODO refactor this together with straight file
            String extension = FileUtils.getExtension(ic.getFile());

            if ((manager.getArchiveHandler() != null) && manager.getArchiveHandler().canUnPack(extension)) {
                indexArchive(ic, extension);
            } else {
                // handle straight files
                if (ic.getFile().isFile()) {
                    if (!FileUtils.isLink(ic.getFile())) {
                        indexStraightFile(ic);
                    } else {
                        log.warn("Skipping symbolic link: " + ic.getFile().getAbsolutePath());
                    }
                } else {
                    log.debug("not a normal file: " + ic.getFile().getName());
                }
            }
        }
    }

    /**
     * @param ic
     * @throws IndexException
     */
    private void indexStraightFile(final IndexCommand ic) throws IndexException {
        log.debug(ic.getFile() + " is a straight file");
        if (!ic.isInZip()) {
            ic.setRealName(ic.getFile().getName());
        } else {
            ic.setZipName(ic.getFile().getName());
        }
        // do we support this kind of file?
        if (manager.getFactory().canExtract(ic.getFile()) || manager.getFactory().isDefaultFileinfo()) {
            // get the hash for this file
            String hash = FileUtils.getMD5Hash(ic.getFile());
            // if we can't get a hash, just set it to a non null value,
            // so at least the indexing continues
            if (hash == null) {
                hash = "unknown";
            }
            // Check whether this file has been added already
            if (!ic.getCollection().getMd5DocumentCache().contains(hash)) {
                // new document, handle it
                ic.setHash(hash);
                Document doc = parse(ic);
                if (doc != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Indexcommand: " + ic);
                    }
                    // add the document to the index(writer)
                    try {
                        ic.getWriter().addDocument(doc);

                        // add the hash to hashtable if not "unknown" or
                        // empty
                        if (!"unknown".equals(hash) && (hash.length() > 0)) {
                            boolean result = ic.getCollection().getMd5DocumentCache().add(hash);

                            if (result) {
                                log.debug("Hash added for document: " + ic.getFile());
                            } else {
                                log.warn("No Hash added for document: " + ic.getFile());
                            }
                        }

                        log.info("document #" + ic.getWriter().docCount() + ": " + ic.getFile().getName() + " added to index");
                    }
                    catch (IOException e) {
                        throw new IndexException("Error adding document '" + ic.getFile().getName() + "' to Index", e);
                    }
                }
            } else {
                log.info("skipping duplicate document: " + ic.getFile().getName());

                // if this document is in the cache, we may remove it
                if (FileUtils.isIn(ic.getFile(), ic.getCollection().getCacheDirWithManagerDefaults())) {
                    if (ic.getFile().delete()) {
                        log.debug("Removed: " + ic.getFile() + " from cache.");
                    }
                }
            }
        } else {
            log.debug("skipping unsupported document: " + ic.getFile().getName());
        }
    }

    /**
     * @param ic
     * @param extension
     * @throws IndexException
     */
    private void indexArchive(final IndexCommand ic, String extension) throws IndexException {
        if (stopRequested) {
            log.info("Indexing stops, due to request");
            return;
        }
        // we have an archive
        log.debug(ic.getFile() + " is an archive");
        // add the document with just its name and hash to the collection as well, so that we can cache it
        // for incremental indexing
        String hash = FileUtils.getMD5Hash(ic.getFile());
        // if we can't get a hash, just set it to a non null value, so at least the indexing continues
        if (hash == null) {
            hash = "unknown";
        }
        // Check whether this file has been added already
        if (!ic.getCollection().getMd5DocumentCache().contains(hash)) {
            try {
                // add the document with just its name and hash
                Document doc = new Document();
                doc.add(new Field("hash", hash, Field.Store.YES, Field.Index.UN_TOKENIZED));
                doc.add(new Field("name", ic.getRealName(), Field.Store.YES, Field.Index.TOKENIZED));
                ic.getWriter().addDocument(doc);
                log.debug("Archive " + ic.getFile() + " added to collection");
                File dir = null;
                if (!StringUtils.hasText(manager.getArchiveHandler().getUnArchiveCommand(extension))) {
                    // this is a zip: handle with java's zip
                    // capabilities
                    log.debug(ic.getFile() + " is a zip file");
                    dir = CollectionManagerImpl.unZip(ic.getFile(), ic.getCollection());
                } else {
                    log.debug(ic.getFile() + " is a external archive file");
                    dir = manager.unPack(ic.getFile(), ic.getCollection());
                }

                IndexCommand localIc = new IndexCommand(ic);
                if (ic.isInZip()) {
                    // ic.setZipPath(ic.getZipPath() +
                    // ic.getFile().getName() + "::/");
                    localIc.setZipPath(ic.getZipPath() + dir.getName() + "/");
                    localIc.setStart(true);
                } else {
                    localIc.setRealName(ic.getFile().getName());
                    localIc.setInZip(true);
                    localIc.setStart(false);
                }

                localIc.setFile(dir);
                indexDocs(localIc);
                // remove dir since it is temporary
                if (!ic.getCollection().isKeepCacheWithManagerDefaults()) {
                    FileUtils.removeDir(dir);
                }

                // add the hash to hashtable if not "unknown" or empty
                if (!"unknown".equals(hash) && (hash.length() > 0)) {
                    boolean result = ic.getCollection().getMd5DocumentCache().add(hash);
                    if (result) {
                        log.debug("Hash added for document: " + ic.getFile());
                    } else {
                        log.warn("No Hash added for document: " + ic.getFile());
                    }
                }
            }
            catch (IOException e) {
                throw new IndexException("Error adding document '" + ic.getFile().getName() + "' to Index", e);
            }
        } else {
            log.info("skipping duplicate archive: " + ic.getFile().getName());
        }
    }

    /**
     * @param ic
     * @throws IndexException
     */
    private void indexDirectory(final IndexCommand ic) throws IndexException {
        if (stopRequested) {
            log.info("Indexing stops, due to request");
            return;
        }
        log.debug(ic.getFile() + " is a directory");
        // recurse
        String[] files = ic.getFile().list();
        // I've seen list return null, so be carefull, guess dir names too long for OS
        if (files == null) {
            log.warn("Something funny with '" + ic.getFile() + "'. Name or path too long?");
            log.warn("Could not access '" + ic.getFile() + "' for indexing. Skipping this directory.");
        } else {

            log.debug(ic.getFile() + " is a directory with " + files.length + " docs");
            if (!ic.isInZip()) {
                // is this the first directory
                if (!ic.isStart()) {
                    ic.setRealPath(ic.getRealPath() + ic.getFile().getName() + "/");
                } else {
                    ic.setStart(false);
                }
            } else {
                if (!ic.isStart()) {
                    ic.setZipPath(ic.getZipPath() + ic.getFile().getName() + "/");
                } else {
                    ic.setStart(false);
                }
            }

            // Index the files using a new IndexCommand that's a copy of the current one
            // except with the new File, don't use the current since status will be overridden
            // when backtracking from recursion
            for (int i = 0; i < files.length; i++) {
                IndexCommand localIc = new IndexCommand(ic);
                localIc.setFile(new File(ic.getFile(), files[i]));
                indexDocs(localIc);
            }
        }
    }

    /**
     * Makes a document for a File, by parsing the contents and metadata provided by {@link IndexCommand}.
     * 
     * @param ic IndexCommand containing all parameters for parsing.
     * 
     * @return Document with parsed content, or null if unknown format, or empty content.
     */
    private Document parse(final IndexCommand ic) {
        log.debug("Parsing " + ic.getFile().getName());

        // Extract relevant info from the file by first getting the relevant
        // Extractor
        Extractor ext = manager.getFactory().createExtractor(ic.getFile());

        if (ext == null) {
            log.debug("Skipping " + ic.getFile().getName());

            return null;
        }

        ParsedFileInfo fileInfo = ext.extractInfo(ic.getFile());

        if (fileInfo != null) {
            // make a new, empty document
            if (log.isDebugEnabled()) {
                log.debug("Creating new Document with ParsedFileInfo: " + fileInfo);
            }

            Document doc = new Document();

            // Add all collection info
            doc.add(new Field("name", ic.getRealName(), Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field("path", ic.getRealPath(), Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field("zipPath", ic.getZipPath(), Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field("zipName", ic.getZipName(), Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field("collection", ic.getCollection().getName(), Field.Store.YES, Field.Index.TOKENIZED));

            // Add all file info
            if (fileInfo.getReader() != null) {
                doc.add(new Field("contents", fileInfo.getReader()));
            }
            doc.add(new Field("summary", fileInfo.getSummary(), Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field("title", fileInfo.getTitle(), Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field("type", fileInfo.getType(), Field.Store.YES, Field.Index.TOKENIZED));
            if (fileInfo.getISBN() != null) {
                doc.add(new Field("isbn", fileInfo.getISBN(), Field.Store.YES, Field.Index.UN_TOKENIZED));
            }

            // store date as yyyyMMdd
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            String dfString = df.format(new Date(fileInfo.getModificationDate()));

            doc.add(new Field("modified", dfString, Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field("size", Long.toString(fileInfo.getSize()), Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field("hash", ic.getHash(), Field.Store.YES, Field.Index.UN_TOKENIZED));

            if (log.isDebugEnabled()) {
                log.debug("Parsed " + doc);
            }

            return doc;
        } else {
            log.warn("Extractor does not return any ParsedFileInfo for: " + ic.getFile().getName());
        }
        return null;
    }

}
