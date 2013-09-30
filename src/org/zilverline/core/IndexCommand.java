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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.index.IndexWriter;

/**
 * Command pattern for providing Indexing process with parameters. The command contains a document that needs to be indexed, as well
 * as the state within the indexing process. The state includes path (real and within possible zip), and the collection.
 * 
 * <p>
 * If the document was in an archive, the archive's name is realName, the name of the document itself is zipName
 * </p>
 * 
 * <p>
 * If the document was <b>not </b> in an archive, the name of the document itself is realName
 * </p>
 * 
 * @author Michael Franken
 * @version $Revision: 1.22 $
 * 
 */
public class IndexCommand {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(IndexCommand.class);

    /** The collection of the document. */
    private FileSystemCollection collection;

    /** The file of the document. */
    private File file;

    /** The MD5 hash of the document. */
    private String hash;

    /** Indicates whether document was originally in an archive. */
    private boolean inZip;

    /** The name of the document. */
    private String realName;

    /** The path of the document. */
    private String realPath;

    /** Indicates whether the indexing process has just started. */
    private boolean start;

    /** The {@link IndexWriter}of the indexing process. */
    private IndexWriter writer;

    /** The name of the document within the zip. */
    private String zipName;

    /** The path of the document within the zip. */
    private String zipPath;

    /**
     * Default constructor initializes all attributes to empty values, or false.
     */
    public IndexCommand() {
        realName = "";
        realPath = "";
        zipName = "";
        zipPath = "";
        hash = "";
        inZip = false;
        start = false;
    }

    /**
     * Copy constructor initializes all attributes.
     * 
     * @param otherCommand an IndexCommand to copy values from
     */
    public IndexCommand(final IndexCommand otherCommand) {
        writer = otherCommand.getWriter();
        file = otherCommand.getFile();
        realName = otherCommand.getRealName();
        hash = otherCommand.getHash();
        realPath = otherCommand.getRealPath();
        zipName = otherCommand.getZipName();
        zipPath = otherCommand.getZipPath();
        collection = otherCommand.getCollection();
        inZip = otherCommand.isInZip();
        start = otherCommand.isStart();
    }

    /**
     * Get the collection this document belongs to.
     * 
     * @return the collection
     */
    public final FileSystemCollection getCollection() {
        return collection;
    }

    /**
     * Get the file.
     * 
     * @return the file that is being indexed
     */
    public final File getFile() {
        return file;
    }

    /**
     * Get the MD5 hash of the file being indexed.
     * 
     * @return the MD5hash of the file.
     */
    public final String getHash() {
        return hash;
    }

    /**
     * Get the realname, the name of this file.
     * 
     * @return String
     */
    public final String getRealName() {
        return realName;
    }

    /**
     * Get the realpath, the path within the indexing process of this file.
     * 
     * @return String the real path
     */
    public final String getRealPath() {
        return realPath;
    }

    /**
     * Get the IndexWriter for this porcess.
     * 
     * @return IndexWriter the indexwriter
     */
    public final IndexWriter getWriter() {
        return writer;
    }

    /**
     * Get the name of the document within the Archive.
     * 
     * <p>
     * The document was in a archive (with name realName).
     * </p>
     * 
     * @return the name of the document.
     */
    public final String getZipName() {
        return zipName;
    }

    /**
     * Get the path of the document within the Archive.
     * 
     * <p>
     * The document was in a archive (with name realName).
     * </p>
     * 
     * @return the path of the document within the archive.
     */
    public final String getZipPath() {
        return zipPath;
    }

    /**
     * Indicates whether the document came from an archive.
     * 
     * @return true or false
     */
    public final boolean isInZip() {
        return inZip;
    }

    /**
     * Indicates whether the indexing process just started.
     * 
     * <p>
     * This is used to make up the entire path of the document, where the first directory (the collection's basedir, and the
     * archive's expanded directory) is not include in the entire location.
     * </p>
     * 
     * @return true or false
     */
    public final boolean isStart() {
        return start;
    }

    /**
     * Set the collection this document belongs to.
     * 
     * @param col The collection of the document.
     */
    public final void setCollection(final FileSystemCollection col) {
        collection = col;
    }

    /**
     * Set this file for this document.
     * 
     * @param thisFile The File
     */
    public final void setFile(final File thisFile) {
        this.file = thisFile;
    }

    /**
     * Set the MD5 hash for this document.
     * 
     * @param string the MD5 hash
     */
    public final void setHash(final String string) {
        hash = string;
    }

    /**
     * Set whether this document came from an archive.
     * 
     * @param b true or false
     */
    public final void setInZip(final boolean b) {
        inZip = b;
    }

    /**
     * Set the real name of the document.
     * 
     * <p>
     * This is the name of the original file provided to the indexing process; either a non-archive, or the archive itself.
     * </p>
     * 
     * <p>
     * The path and name make up the location of the document.
     * </p>
     * 
     * @param string The name of the document
     */
    public final void setRealName(final String string) {
        realName = string;
    }

    /**
     * Set the real path of the document.
     * 
     * <p>
     * This is the path of the original file provided to the indexing process; either a non-archive, or the archive itself.
     * </p>
     * 
     * <p>
     * The path and name make up the location of the document.
     * </p>
     * 
     * @param string The path of the document
     */
    public final void setRealPath(final String string) {
        realPath = string;
    }

    /**
     * Sets whether the indexing process just started.
     * 
     * <p>
     * This is used to make up the entire path of the document, where the first directory (the collection's basedir, and the
     * archive's expanded directory) is not include in the entire location.
     * </p>
     * 
     * @param b true or false
     */
    public final void setStart(final boolean b) {
        start = b;
    }

    /**
     * Set the {@link IndexWriter}of the indexing process.
     * 
     * @param thisWriter the IndexWriter
     */
    public final void setWriter(final IndexWriter thisWriter) {
        this.writer = thisWriter;
    }

    /**
     * Set the name of the document within the archive.
     * 
     * <p>
     * This is the name of the file within the archive;
     * </p>
     * 
     * <p>
     * The zipPath and zipName make up the location of the document within the archive.
     * </p>
     * 
     * @param string The name of the document within the archive
     */
    public final void setZipName(final String string) {
        zipName = string;
    }

    /**
     * Set the path of the document within the archive.
     * 
     * <p>
     * This is the path of the file within the archive;
     * </p>
     * 
     * <p>
     * The zipPath and zipName make up the location of the document within the archive.
     * </p>
     * 
     * @param string The path of the document within the archive
     */
    public final void setZipPath(final String string) {
        zipPath = string;
    }

    /**
     * Prints IndexCommand as String for logging.
     * 
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        return "File: " + file + " in collection '" + collection.getName() + "' with hash: " + hash + "\n\tPath: " + realPath + "<"
            + realName + ">" + "\n\tzipPath: " + zipPath + "<" + zipName + ">, inZip: " + inZip + ", start: " + start;
    }
}
