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

package org.zilverline.dao.xstream;

import java.io.File;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of DAO interface using XStream support.
 * 
 * @author Michael Franken
 * @version $Revision: 1.6 $
 */
public abstract class AbstractXStreamDAOImpl {
    /** logger for Commons logging. */
    protected Log log = LogFactory.getLog(this.getClass());

    /** XStream object for XML serialization. */
    protected XStream xstream;

    /** Writer used for writing XStream objects to file. */
    protected Writer writer;

    /** Reader used for reading XStream objects from file. */
    protected Reader reader;

    /** Filename used for reading and writing XStream objects to and from file. */
    protected String filename;

    /**
     * Create a new one. Used by subclasses only. Use the application's location as base directory for DAO persistency files. In a
     * web application this will be WEB-INF/db.
     */
    public AbstractXStreamDAOImpl() {
        xstream = new XStream();
        // find the application's classpath
        baseDir = new File(new File(AbstractXStreamDAOImpl.class.getResource("/").getFile()).getParentFile(), "db");
        if (!baseDir.isDirectory()) {
            baseDir.mkdirs();
        }
    }

    /**
     * The base directory for DAO persistency files.
     */
    protected File baseDir;

    /**
     * Get the base directory for writing and reading files.
     * 
     * @return Returns the baseDir.
     */
    public File getBaseDir() {
        return baseDir;
    }

    /**
     * @param thatBaseDir The baseDir to set.
     */
    public void setBaseDir(final File thatBaseDir) {
        this.baseDir = thatBaseDir;
        if (!baseDir.isDirectory()) {
            baseDir.mkdirs();
        }
    }

    /**
     * @return Returns the filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename The filename to set.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
